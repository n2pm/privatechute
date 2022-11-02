package pm.n2.parachute.util;

import pm.n2.parachute.config.Configs;

public class FakeRandom {
    public static long getHashCode(int x, int y, int z) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_NO_ROTATIONS.getBooleanValue()) {
            return 0;
        }
        long l = ((long) x * (98423333394L)) ^ (long) y * (9847464L) ^ (long) z;
        l = l * l * 9439499849348L + l * 73L;
        return l >> 16;
    }
}
