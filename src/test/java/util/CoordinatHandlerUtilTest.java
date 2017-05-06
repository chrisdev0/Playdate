package util;

import org.junit.Test;
import utils.CoordinateHandlerUtil;

import static org.junit.Assert.*;

public class CoordinatHandlerUtilTest {

    @Test
    public void testCorrectLatLonToGrid() {
        String N = "59.40738";
        String E = "17.945781";
        int gridX = 6589373;
        int gridY = 1621564;
        CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
        double[] doubles = coordinateHandlerUtil.geodeticToGrid(Double.parseDouble(N), Double.parseDouble(E));
        assertEquals(doubles[0], (double) gridX, 1);
        assertEquals(doubles[1], (double) gridY, 1);
    }

    @Test
    public void testCorrectGridToLatLon() {
        String N = "59.40738";
        String E = "17.945781";
        int gridX = 6589373;
        int gridY = 1621564;
        CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
        double[] doubles = coordinateHandlerUtil.gridToGeodetic(gridX, gridY);
        assertEquals(doubles[0], Double.parseDouble(N), 1);
        assertEquals(doubles[1], Double.parseDouble(E), 1);
    }

    @Test
    public void testCorrectLatLonToGrid2() {
        String N = "59.20738";
        String E = "19.945781";
        int gridX = 6572480;
        int gridY = 1736454;
        CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
        double[] doubles = coordinateHandlerUtil.geodeticToGrid(Double.parseDouble(N), Double.parseDouble(E));
        assertEquals(doubles[0], (double) gridX, 1);
        assertEquals(doubles[1], (double) gridY, 1);
    }

    @Test
    public void testCorrectGridToLatLon2() {
        String N = "59.20738";
        String E = "19.945781";
        int gridX = 6572480;
        int gridY = 1736454;
        CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
        double[] doubles = coordinateHandlerUtil.gridToGeodetic(gridX, gridY);
        assertEquals(doubles[0], Double.parseDouble(N), 1);
        assertEquals(doubles[1], Double.parseDouble(E), 1);
    }

    @Test
    public void incorrectToLatLon() {
        String N = "59.20738";
        String E = "19.945781";
        int gridX = 100000;
        int gridY = 200000;
        CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
        double[] doubles = coordinateHandlerUtil.gridToGeodetic(gridX, gridY);
        assertNotEquals(doubles[0], Double.parseDouble(N), 1);
        assertNotEquals(doubles[1], Double.parseDouble(E), 1);
    }

    @Test
    public void incorrectToGrid() {
        String N = "29.20738";
        String E = "12.945781";
        int gridX = 6572480;
        int gridY = 1736454;
        CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
        double[] doubles = coordinateHandlerUtil.gridToGeodetic(gridX, gridY);
        assertNotEquals(doubles[0], Double.parseDouble(N), 1);
        assertNotEquals(doubles[1], Double.parseDouble(E), 1);
    }




}
