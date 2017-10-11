package terrails.statskeeper.api.helper;

public class StringHelper {

    public static String getSubstringBefore(String string, String index) {
        return string.substring(0, string.indexOf(index));
    }
    public static String getSubstringAfter(String string, String index) {
        return string.substring(string.indexOf(index));
    }

    /*
    @SuppressWarnings("deprecation")
    public static IBlockState generatingBlock(String string) {
        if (!string.isEmpty()) {
            int metadata = string.contains("-meta:") ? generatingBlockMeta(string) : 0;
            String blockString = string.substring(0, string.indexOf(" -"));
            Block theBlock = Block.getBlockFromName(blockString);
            IBlockState blockState = theBlock != null ? theBlock.getStateFromMeta(metadata) : null;
            return blockState;
        }
        return null;
    }
    public static int generatingBlockMeta(String string) {
        if (string.contains("-meta:")) {
            String metaDataString = string.replaceAll("^.*(-meta:\\d+).*$", "$1");
            String metaDataDigit = CharMatcher.DIGIT.retainFrom(metaDataString);
            return Integer.parseInt(metaDataDigit);
        }
        else return 0;
    }

    @SuppressWarnings("deprecation")
    public static Block replaceBlock(String string) {
        if (string.contains("-replace:")) {
            String replace1 = string.substring(string.indexOf("-replace:")).replace("-replace:", "");
            String replace2 = replace1.contains(" -") ? replace1.replaceAll("([\\s]).*", "$1").replace(" ", "")
                    : StringUtils.substringBefore(replace1, ";");//replace1.replace(";", "");

            if (replace2.contains("|")) {
                // Multiple replace blocks don't work so this isn't usable but its here just because reasons!
                Block replaceBlock = null;
                String[] splitArray = replace2.split("\\|");
                for (String replace : splitArray) {
                    int metadata = replace.contains("-meta:") ? replacingBlockMetadata(replace) : 0;
                    String theReplaceBlock = replace.contains("-meta:") ? replace.substring(0, replace.indexOf("-meta:")) : replace;
                    Block theBlock = Block.getBlockFromName(theReplaceBlock);
                    replaceBlock = theBlock != null ? theBlock.getStateFromMeta(metadata).getBlock() : null;
                }
                return replaceBlock;
            }

            else if (!replace2.contains("|")) {
                int metadata = replace2.contains("-meta:") ? replacingBlockMetadata(replace2) : 0;
                String theReplaceBlock = replace2.contains("-meta:") ? replace2.substring(0, replace2.indexOf("-meta:")) : replace2;
                Block theBlock = Block.getBlockFromName(theReplaceBlock);
                return theBlock != null ? theBlock.getStateFromMeta(metadata).getBlock() : null;
            }
        }
        return null;
    }
    private static int replacingBlockMetadata(String string) {
        if (string.contains("-meta:")) {
            String metaDataString = string.replaceAll("^.*(-meta:\\d+).*$", "$1");
            String metaDataDigit = CharMatcher.DIGIT.retainFrom(metaDataString);
            int metaData = Integer.parseInt(metaDataDigit);
            return metaData;
        }
        else return 0;
    }

    public static int minVein(String string) {
        if (string.contains("-minvein:")) {
            String stringMinVein = string.replaceAll("^.*( -minvein:\\d+).*$", "$1");
            String digitMinVein = CharMatcher.DIGIT.retainFrom(stringMinVein);
            int minVein = Integer.parseInt(digitMinVein);
            return minVein;
        }
        return 0;
    }
    public static int maxVein(String string) {
        if (string.contains("-maxvein:")) {
            String stringMaxVein = string.replaceAll("^.*( -maxvein:\\d+).*$", "$1");
            String digitMaxVein = CharMatcher.DIGIT.retainFrom(stringMaxVein);
            int maxVein = Integer.parseInt(digitMaxVein);
            return maxVein;
        }
        return 0;
    }

    public static int minY(String string) {
        if (string.contains("-miny:")) {
            String stringMinY = string.replaceAll("^.*( -miny:\\d+).*$", "$1");
            String digitMinY = CharMatcher.DIGIT.retainFrom(stringMinY);
            int minY = Integer.parseInt(digitMinY);
            return minY;
        }
        return 0;
    }
    public static int maxY(String string) {
        if (string.contains("-maxy:")) {
            String stringMaxY = string.replaceAll("^.*( -maxy:\\d+).*$", "$1");
            String digitMaxY = CharMatcher.DIGIT.retainFrom(stringMaxY);
            int maxY = Integer.parseInt(digitMaxY);
            return maxY;
        }
        return 0;
    }

    public static int perChunk(String string) {
        if (string.contains("-perchunk:")) {
            String stringPerChunk = string.replaceAll("^.*( -perchunk:\\d+).*$", "$1");
            String digitPerChunk = CharMatcher.DIGIT.retainFrom(stringPerChunk);
            int perChunk = Integer.parseInt(digitPerChunk);
            return perChunk;
        }
        return 0;
    }

    public static int biomeID(String string) {
        if (string.contains("-biome:")) {
            String biome1 = string.substring(string.indexOf("-biome:")).replace("-biome:", "");
            String biome2 = biome1.contains(" -") ? biome1.substring(0, biome1.indexOf(" ")) :
                    StringUtils.substringBefore(biome1, ";");//biome1.replace(";", "");
            if (!biome2.contains("|")) {
                int biomeID = Integer.parseInt(biome2);
                return biomeID;
            } else if (biome2.contains("|")) {
                int biID = 0;
                String[] biome3 = biome2.split("\\|");
                for (String biome : biome3) {
                    int biomeID = Integer.parseInt(biome);
                    biID = biomeID;
                }
                return biID;
            }
        }
        return 0;
    }
    public static int dimensionID(String string) {
        if (string.contains("-dimension:")) {
            String dim1 = string.substring(string.indexOf("-dimension:")).replace("-dimension:", "");
            String dim2 = dim1.contains(" -") ? dim1.substring(0, dim1.indexOf(" ")) :
                    StringUtils.substringBefore(dim1, ";");//dim1.replace(";", "");
            if (!dim2.contains("|")) {
                return Integer.parseInt(dim2);

            }
            else if (dim2.contains("|")) {
                int dimID = 0;
                String[] dim3 = dim2.split("\\|");
                for (String dim : dim3) {
                    dimID = Integer.parseInt(dim);
                }
                return dimID;
            }
        }
        return 0;
    }*/
}
