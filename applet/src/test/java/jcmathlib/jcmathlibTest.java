package jcmathlib;

import jcmathlib.jcmathlib.ECConfig;
import jcmathlib.jcmathlib.ECCurve;
import jcmathlib.jcmathlib.ECPoint;
import jcmathlib.jcmathlib.SecP256r1;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * Test for compatibility with JCMathLib 1.0.1
 * https://github.com/OpenCryptoProject/JCMathLib/releases/tag/v1.0.1
 */
public class jcmathlibTest extends BaseTest {

    /**
     * Test example usage of JCMathLib according to `ECExample.java`, running in JUnit
     * https://github.com/OpenCryptoProject/JCMathLib/blob/1217f42ac26669a4a3cf5d9b4151ecd1f576487b/JCMathLib/src/opencrypto/jcmathlib/ECExample.java
     */
    @Test
    public void exampleUsageInJUnit() {
        ECConfig ecc;
        ECCurve curve;
        ECPoint point1;
        ECPoint point2;
        final byte[] ECPOINT_TEST_VALUE = {(byte) 0x04, (byte) 0x3B, (byte) 0xC1, (byte) 0x5B, (byte) 0xE5, (byte) 0xF7, (byte) 0x52, (byte) 0xB3, (byte) 0x27, (byte) 0x0D, (byte) 0xB0, (byte) 0xAE, (byte) 0xF2, (byte) 0xBC, (byte) 0xF0, (byte) 0xEC, (byte) 0xBD, (byte) 0xB5, (byte) 0x78, (byte) 0x8F, (byte) 0x88, (byte) 0xE6, (byte) 0x14, (byte) 0x32, (byte) 0x30, (byte) 0x68, (byte) 0xC4, (byte) 0xC4, (byte) 0x88, (byte) 0x6B, (byte) 0x43, (byte) 0x91, (byte) 0x4C, (byte) 0x22, (byte) 0xE1, (byte) 0x67, (byte) 0x68, (byte) 0x3B, (byte) 0x32, (byte) 0x95, (byte) 0x98, (byte) 0x31, (byte) 0x19, (byte) 0x6D, (byte) 0x41, (byte) 0x88, (byte) 0x0C, (byte) 0x9F, (byte) 0x8C, (byte) 0x59, (byte) 0x67, (byte) 0x60, (byte) 0x86, (byte) 0x1A, (byte) 0x86, (byte) 0xF8, (byte) 0x0D, (byte) 0x01, (byte) 0x46, (byte) 0x0C, (byte) 0xB5, (byte) 0x8D, (byte) 0x86, (byte) 0x6C, (byte) 0x09};
        final byte[] SCALAR_TEST_VALUE = {(byte) 0xE8, (byte) 0x05, (byte) 0xE8, (byte) 0x02, (byte) 0xBF, (byte) 0xEC, (byte) 0xEE, (byte) 0x91, (byte) 0x9B, (byte) 0x3D, (byte) 0x3B, (byte) 0xD8, (byte) 0x3C, (byte) 0x7B, (byte) 0x52, (byte) 0xA5, (byte) 0xD5, (byte) 0x35, (byte) 0x4C, (byte) 0x4C, (byte) 0x06, (byte) 0x89, (byte) 0x80, (byte) 0x54, (byte) 0xB9, (byte) 0x76, (byte) 0xFA, (byte) 0xB1, (byte) 0xD3, (byte) 0x5A, (byte) 0x10, (byte) 0x91};

        // Pre-allocate all helper structures
        ecc = new ECConfig((short) 256);
        // Pre-allocate standard SecP256r1 curve and two EC points on this curve
        curve = new ECCurve(false, SecP256r1.p, SecP256r1.a, SecP256r1.b, SecP256r1.G, SecP256r1.r);
        point1 = new ECPoint(curve, ecc.ech);
        point2 = new ECPoint(curve, ecc.ech);

        ecc.refreshAfterReset(); // select applet

        // Generate first point at random
        point1.randomize();
        // Set second point to predefined value
        point2.setW(ECPOINT_TEST_VALUE, (short) 0, (short) ECPOINT_TEST_VALUE.length);
        // Add two points together
        point1.add(point2);
        // Multiply point by large scalar
        // TODO BUG mvondracek: `ECPoint#multiplication` hangs in `jcmathlib.jcmathlib.Bignat.sqrt_FP`
        //      on line 1709 in while loop.
        point1.multiplication(SCALAR_TEST_VALUE, (short) 0, (short) SCALAR_TEST_VALUE.length);

        ecc.refreshAfterReset(); // deselect applet
    }

    /**
     * Test example usage of JCMathLib according to `ECExample.java`, running in simulated Applet
     * https://github.com/OpenCryptoProject/JCMathLib/blob/1217f42ac26669a4a3cf5d9b4151ecd1f576487b/JCMathLib/src/opencrypto/jcmathlib/ECExample.java
     */
    @Test
    public void exampleUsageInApplet() throws Exception {
        final CommandAPDU cmd = new CommandAPDU(0x00, 0x90, 0, 0);
        // TODO BUG mvondracek: `ECPoint#multiplication` hangs in `jcmathlib.jcmathlib.Bignat.sqrt_FP`
        //      on line 1709 in while loop.
        final ResponseAPDU responseAPDU = connect(ECExample.class).transmit(cmd);
        Assert.assertNotNull(responseAPDU);
        Assert.assertEquals(0x9000, responseAPDU.getSW());
        Assert.assertNotNull(responseAPDU.getBytes());
    }
}
