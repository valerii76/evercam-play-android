package io.evercam.androidapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.evercam.Vendor;
import io.evercam.androidapp.ScanActivity;
import io.evercam.androidapp.dto.AppData;
import io.evercam.androidapp.feedback.ScanFeedbackItem;
import io.evercam.androidapp.utils.Commons;
import io.evercam.androidapp.utils.NetInfo;
import io.evercam.network.Constants;
import io.evercam.network.EvercamDiscover;
import io.evercam.network.discovery.DiscoveredCamera;
import io.evercam.network.discovery.GatewayDevice;
import io.evercam.network.discovery.IpScan;
import io.evercam.network.discovery.MacAddress;
import io.evercam.network.discovery.NatMapEntry;
import io.evercam.network.discovery.NetworkInfo;
import io.evercam.network.discovery.PortScan;
import io.evercam.network.discovery.ScanRange;
import io.evercam.network.discovery.ScanResult;
import io.evercam.network.discovery.UpnpDevice;
import io.evercam.network.discovery.UpnpDiscovery;
import io.evercam.network.discovery.UpnpResult;
import io.evercam.network.onvif.OnvifDiscovery;
import io.evercam.network.query.EvercamQuery;

public class ScanForCameraTask extends AsyncTask<Void, DiscoveredCamera, ArrayList<DiscoveredCamera>>
{
    private final String TAG = "ScanForCameraTask";

    private WeakReference<ScanActivity> scanActivityReference;
    private NetInfo netInfo;
    private Date startTime;
    public ExecutorService pool;
    public static ArrayList<DiscoveredCamera> cameraList;
    private boolean upnpDone = false;
    private boolean natDone = false;
    private boolean onvifDone = false;

    //Check if single IP scan and port scan is completed or not by comparing the start and end count
    private int singleIpStartedCount = 0;
    private int singleIpEndedCount = 0;

    private String externalIp = "";

    public ScanForCameraTask(ScanActivity scanActivity)
    {
        this.scanActivityReference = new WeakReference<>(scanActivity);
        netInfo = new NetInfo(scanActivity);
        pool = Executors.newFixedThreadPool(EvercamDiscover.DEFAULT_FIXED_POOL);
        cameraList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute()
    {

    }

    @Override
    protected ArrayList<DiscoveredCamera> doInBackground(Void... params)
    {
        startTime = new Date();
        try
        {
            ScanRange scanRange = new ScanRange(netInfo.getGatewayIp(), netInfo.getNetmaskIp());

            externalIp = NetworkInfo.getExternalIP();

            if(!pool.isShutdown())
            {
                pool.execute(new OnvifRunnable());
                pool.execute(new UpnpRunnable());
                pool.execute(new NatRunnable(netInfo.getGatewayIp()));
            }

            IpScan ipScan = new IpScan(new ScanResult(){
                @Override
                public void onActiveIp(String ip)
                {
                    if(!pool.isShutdown())
                    {
                        pool.execute(new IpScanRunnable(ip));
                    }
                }
            });
            ipScan.scanAll(scanRange);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        while(!onvifDone && !upnpDone || ! natDone || singleIpStartedCount != singleIpEndedCount)
        {
            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return cameraList;
    }

    @Override
    protected void onProgressUpdate(DiscoveredCamera... discoveredCameras)
    {
        scanActivityReference.get().addNewCameraToResultList(discoveredCameras[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<DiscoveredCamera> cameraList)
    {
        Float scanningTime = Commons.calculateTimeDifferenceFrom(startTime);
        Log.d(TAG, "Scanning time: " + scanningTime);

        String username = "";
        if(AppData.defaultUser != null)
        {
            username = AppData.defaultUser.getUsername();
        }
        new ScanFeedbackItem(scanActivityReference.get(), username, scanningTime, cameraList).sendToKeenIo();

        scanActivityReference.get().showProgress(false);

        scanActivityReference.get().showScanResults(cameraList);
    }

    private class OnvifRunnable implements Runnable
    {
        @Override
        public void run()
        {
            new OnvifDiscovery(){
                @Override
                public void onActiveOnvifDevice(DiscoveredCamera discoveredCamera)
                {
                    discoveredCamera.setExternalIp(externalIp);
                    publishProgress(discoveredCamera);
                }
            }.probe();

            onvifDone = true;
        }
    }

    private class IpScanRunnable implements Runnable
    {
        private String ip;

        public IpScanRunnable(String ip)
        {
            this.ip = ip;
            singleIpStartedCount++;
        }

        @Override
        public void run()
        {
            try
            {
                String macAddress = MacAddress.getByIpAndroid(ip);
                if (!macAddress.equals(Constants.EMPTY_MAC))
                {
                    Vendor vendor = EvercamQuery.getCameraVendorByMac(macAddress);
                    if (vendor != null)
                    {
                        String vendorId = vendor.getId();
                        if (!vendorId.isEmpty())
                        {
                            // Then fill details discovered from IP scan
                            DiscoveredCamera camera = new DiscoveredCamera(ip);
                            camera.setMAC(macAddress);
                            camera.setVendor(vendorId);
                            camera.setExternalIp(externalIp);

                            // Start port scan
                            PortScan portScan = new PortScan(null);
                            portScan.start(ip);
                            ArrayList<Integer> activePortList = portScan.getActivePorts();

                            if(activePortList.size() > 0)
                            {
                                // Add active ports to camera object
                                for (Integer port : activePortList)
                                {
                                    camera = PortScan.mergePort(camera, port);
                                }
                                publishProgress(camera);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            singleIpEndedCount ++;
        }
    }

    private class UpnpRunnable implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                UpnpDiscovery upnpDiscovery = new UpnpDiscovery(new UpnpResult(){

                    @Override
                    public void onUpnpDeviceFound(UpnpDevice upnpDevice)
                    {
                        // If IP address matches
                        String ipFromUpnp = upnpDevice.getIp();
                        if (ipFromUpnp != null && !ipFromUpnp.isEmpty())
                        {
                            for(DiscoveredCamera discoveredCamera : cameraList)
                            {
                                if(discoveredCamera.getIP().equals(upnpDevice.getIp()))
                                {
                                    DiscoveredCamera publishCamera = new DiscoveredCamera(discoveredCamera.getIP());
                                    int port = upnpDevice.getPort();
                                    String model = upnpDevice.getModel();
                                    if (port != 0)
                                    {
                                        publishCamera.setHttp(port);
                                    }
                                    publishCamera.setModel(model);
                                    publishProgress(publishCamera);
                                    break;
                                }
                            }
                        }
                    }
                });
                upnpDiscovery.discoverAll();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            upnpDone = true;
        }
    }

    private class NatRunnable implements Runnable
    {
        private String routerIp;

        public NatRunnable(String routerIp)
        {
            this.routerIp = routerIp;
        }

        @Override
        public void run()
        {
            try
            {
                GatewayDevice gatewayDevice = new GatewayDevice(routerIp);
                ArrayList<NatMapEntry> mapEntries = gatewayDevice.getNatTableArray(); //NAT Table

                if(mapEntries.size() > 0)
                {
                    for(NatMapEntry mapEntry : mapEntries)
                    {
                        String natIp = mapEntry.getIpAddress();


                        for(DiscoveredCamera discoveredCamera : cameraList)
                        {
                            if(discoveredCamera.getIP().equals(natIp))
                            {
                                int natInternalPort = mapEntry.getInternalPort();
                                int natExternalPort = mapEntry.getExternalPort();

                                DiscoveredCamera publishCamera = new DiscoveredCamera(natIp);
                                if(discoveredCamera.getHttp() == natInternalPort)
                                {
                                    publishCamera.setExthttp(natExternalPort);
                                }
                                if(discoveredCamera.getRtsp() == natInternalPort)
                                {
                                    publishCamera.setExtrtsp(natExternalPort);
                                }

                                publishProgress(publishCamera);

                                break; //break the inner loop
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            natDone = true;
        }
    }
}
