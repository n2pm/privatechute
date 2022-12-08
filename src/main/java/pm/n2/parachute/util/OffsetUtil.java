package pm.n2.parachute.util;

import net.minecraft.util.math.MathHelper;

public class OffsetUtil {

    public static final int offsetX;
    public static final int offsetZ;

    static {
        // Pick a random offset at init
        offsetX = (int) Math.floor(Math.random() * 60000000);
        offsetZ = (int) Math.floor(Math.random() * 60000000);
    }

    public static double offset(double orig, double offset) {
        return ((orig + offset + 30000000) % 60000000) - 30000000;
    }

    public static int offset(int orig, int offset) {
        return ((orig + offset + 30000000) % 60000000) - 30000000;
    }

    public static long getHashCode(int x, int y, int z) {
        x = offset(x, offsetX);
        z = offset(z, offsetZ);
        return MathHelper.hashCode(x, y, z);
    }
}
