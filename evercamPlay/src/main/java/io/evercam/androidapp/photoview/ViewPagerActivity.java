package io.evercam.androidapp.photoview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import io.evercam.androidapp.ParentAppCompatActivity;
import io.evercam.androidapp.R;
import uk.co.senab.photoview.PhotoView;

public class ViewPagerActivity extends ParentAppCompatActivity
{
	private ViewPager mViewPager;
	private static String[] mImagePaths = {};
	
    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_image);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

		setUpGradientToolbarWithHomeButton();

		mViewPager.setAdapter(new SamplePagerAdapter());

		updateTitleText("1 of " + mImagePaths.length); //Initial title as 1 of total

		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int
					positionOffsetPixels)
			{}

			@Override
			public void onPageSelected(int position)
			{
				int pageNumber = position + 1;
				updateTitleText(pageNumber + " of " + mImagePaths.length);
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{}
		});
	}

	static class SamplePagerAdapter extends PagerAdapter
	{
		@Override
		public int getCount()
		{
			return mImagePaths.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position)
		{
			PhotoView photoView = new PhotoView(container.getContext());
			photoView.setImageURI(Uri.parse(mImagePaths[position]));

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == object;
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:

				finish();

			default:
				return super.onOptionsItemSelected(item);
		}
	}
    
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	/**
	 * Call this method to launch ViewPagerActivity by passing the image path array
	 *
	 * @param activity The previous activity that launch the ViewPagerActivity
	 * @param imagePaths Snapshots image path string array
	 */
	public static void showSavedSnapshots(Activity activity, String[] imagePaths)
	{
		mImagePaths = imagePaths;
		Intent intent = new Intent(activity, ViewPagerActivity.class);
		activity.startActivity(intent);
	}
}
