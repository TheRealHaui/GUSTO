/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2016 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package esa.esac.gusto.gui.projection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.gui.projection.HammerProjection;
import esa.esac.gusto.gui.projection.SkyProjection;
import esa.esac.gusto.math.Attitude;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector2;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

public class HammerProjectionTest {

    @Test
    public void testConstructors() {
	Quaternion q1 = Attitude.fromDegrees(20,30,40).toQuaternion();
	SkyProjection proj = new HammerProjection(q1);
	Quaternion q2 = proj.getViewPort();
	assertTrue(q1.epsilonEquals(q2, 1E-15));
    }
    
    @Test
    public void testName() {
	SkyProjection p = new HammerProjection();
	assertTrue(p.getName().equals("Hammer"));
    }
    
    @Test
    public void testDefaultViewport() {
	SkyProjection proj = new HammerProjection();
	double scale = 3;
	proj.setScale(scale);
	
	// Forward projection normal case
	Vector3 v1 = new Vector3(3,2,1);

	// Test round trip but actual result because this would just duplicate algorithm.
	// Round trip is a good test because forward and inverse are implemented differently.
	Vector3 v1n = v1.normalize();
	Vector2 p1 = proj.forward(v1);
	Vector3 r1 = proj.inverse(p1);
	assertTrue(r1.epsilonEquals(v1n, 1E-12));
	
	// Test near edge
	Vector3 v3 = new Vector3(Direction.fromDegrees(30, 89.95));
	Vector3 v3n = v3.normalize();
	Vector2 p3 = proj.forward(v3);
	Vector3 r3 = proj.inverse(p3);
	assertTrue(r3.epsilonEquals(v3n, 1E-1));
    }
    
    @Test
    public void testGetSetScale() {
	SkyProjection proj = new HammerProjection();
	assertEquals(300, proj.getScale(), 1E-15); // Default scale

	double scale = 3;
	proj.setScale(scale);
	assertEquals(scale, proj.getScale(), 1E-15);
    }
    
    @Test
    public void testScalingFactor() {
	SkyProjection proj = new HammerProjection();
	double scale = 3;
	proj.setScale(scale);

	double dx = 0.0002;
	double dy = 0.0003;
	Vector3 v1 = new Vector3(Math.sqrt(1 - dx*dx - dy*dy), dx, dy).normalize();
	Vector2 p1 = proj.forward(v1);
	double x = -Math.atan(p1.getX());
	double y = Math.atan(p1.getY());

	assertEquals(dx * scale, x, 1E-9);
	assertEquals(dy * scale, y, 1E-9);
    }
    
    @Test
    public void testGetSetViewPort() {
	SkyProjection proj = new HammerProjection();
	
	// Check default viewport
	Quaternion q0 = proj.getViewPort();
	assertTrue(q0.epsilonEquals(new Quaternion(0,0,0,1), 1E-15));
	
	Quaternion q1 = Attitude.fromDegrees(20,30,40).toQuaternion();
	proj.setViewPort(q1);
	Quaternion q2 = proj.getViewPort();
	assertTrue(q2.epsilonEquals(q1, 1E-15));
    }
    
    @Test
    public void test2() {
	Attitude att = Attitude.fromDegrees(20,30,40);
	Quaternion qView = att.toQuaternion();
	
	SkyProjection proj = new HammerProjection();
	double scale = 3;
	proj.setScale(scale);
	proj.setViewPort(qView);

	Vector3 v1 = new Vector3(1,2,3);
	Vector2 p1 = proj.forward(v1);
	
	// Unset the viewport and pre-transform the vector
	proj.setViewPort(new Quaternion(0,0,0,1));
	Vector3 v2 = qView.rotateAxes(v1);
	Vector2 p2 = proj.forward(v2);
	
        // Compare the results
	assertTrue(p1.epsilonEquals(p2, 1E-15));
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	SkyProjection d = new HammerProjection();
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}


