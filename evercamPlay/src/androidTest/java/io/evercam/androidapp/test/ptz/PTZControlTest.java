package io.evercam.androidapp.test.ptz;

import junit.framework.TestCase;

import io.evercam.androidapp.ptz.PTZBuilder;
import io.evercam.androidapp.ptz.PTZRelative;

public class PTZControlTest extends TestCase
{
    public void testPTZRelativeMove()
    {
        PTZRelative ptzRelative = new PTZRelative(new PTZBuilder("mobile-mast").relativeLeft(10));
        ptzRelative.move();
        assertTrue(true);
    }
}
