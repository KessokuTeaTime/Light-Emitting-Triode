package band.kessokuteatime.lightemittingtriode.content.block.functional.base;

public interface PoweredBlockModelModifiers {
    default String[] poweredBlockStatePrefixes() {
        return new String[]{};
    };
    default String[] poweredBlockStateSuffixes() {
        return new String[]{ "powered" };
    };
}
