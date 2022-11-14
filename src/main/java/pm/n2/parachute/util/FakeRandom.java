package pm.n2.parachute.util;

import net.minecraft.util.math.MathHelper;

public class FakeRandom {

    public static final int offsetX;
    public static final int offsetZ;

    private static final long multiplierX;
    private static final long multiplierZ;
    private static final long multiplierCommon;

    public static Mode mode = Mode.OFFSET;

    static {
        // Pick a random offset at init
        offsetX = (int) Math.floor(Math.random() * 60000000);
        offsetZ = (int) Math.floor(Math.random() * 60000000);

        // Pick random multipliers
        multiplierX = (int) Math.floor(Math.random() * 67108863);
        multiplierZ = (int) Math.floor(Math.random() * 67108863);
        multiplierCommon = (int) Math.floor(Math.random() * 67108863);
    }

    public static double offset(double orig, double offset) {
        return ((orig + offset + 30000000) % 60000000) - 30000000;
    }

    public static int offset(int orig, int offset) {
        return ((orig + offset + 30000000) % 60000000) - 30000000;
    }

    enum Mode {
        DISABLED,
        FAKE_RANDOM,
        NO_RANDOM,
        OFFSET
    }

    public static long getHashCode(int x, int y, int z) {
        long hashCode;
        switch (mode) {
            case FAKE_RANDOM -> {
                long l = (x * multiplierX) ^ (long) z * multiplierZ ^ (long) y;
                l = l * l * multiplierCommon + l * 11L;
                hashCode = l >> 16;
            }
            case NO_RANDOM -> hashCode = 0;
            case OFFSET -> {
                // Wrap around for coordinates out of the world
                x = offset(x, offsetX);
                z = offset(z, offsetZ);
                hashCode = MathHelper.hashCode(x, y, z);
            }
            default -> {
                hashCode = MathHelper.hashCode(x, y, z);
            }
        }
        return hashCode;
    }
}
