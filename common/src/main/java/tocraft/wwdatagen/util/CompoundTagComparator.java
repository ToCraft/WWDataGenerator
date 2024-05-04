package tocraft.wwdatagen.util;

import net.minecraft.nbt.CompoundTag;

import java.util.Comparator;
import java.util.Objects;

/*
 * Why did I create this???
 */
@SuppressWarnings("unused")
public class CompoundTagComparator implements Comparator<CompoundTag> {
    @Override
    public int compare(CompoundTag a, CompoundTag b) {
        int i = advancedCompare(a, b);
        if (i < 2) {
            return i;
        }

        // if the above fails, compare the size
        return Integer.compare(a.getAllKeys().size(), b.getAllKeys().size());
    }

    private static int advancedCompare(CompoundTag a, CompoundTag b) {
        if (Objects.equals(a, b) || a.getAsString().equals(b.getAsString())) {
            return 0;
        }
        if (a.getAllKeys().containsAll(b.getAllKeys())) {
            boolean bool = false;
            for (String key : b.getAllKeys()) {
                if (!Objects.equals(a.get(key), b.get(key))) {
                    bool = true;
                    break;
                }
            }
            if (!bool) return -1;
        }
        if (b.getAllKeys().containsAll(a.getAllKeys())) {
            boolean bool = false;
            for (String key : a.getAllKeys()) {
                if (!Objects.equals(b.get(key), a.get(key))) {
                    bool = true;
                    break;
                }
            }
            if (!bool) return 1;
        }

        return 2;
    }

    public static boolean OneIncludesTheOther(CompoundTag a, CompoundTag b) {
        return advancedCompare(a, b) < 2;
    }
}