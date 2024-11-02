package org.enissay.dungeonssim.utils;

import org.bukkit.ChatColor;

public class FormatUtil {
    public static String format(double number){
        String result = "";
        if (number >= 1000) {
            String[] suffix = new String[]{"K", "M", "B", "T"};
            int size = (number != 0) ? (int) Math.log10(number) : 0;
            if (size >= 3) {
                while (size % 3 != 0) {
                    size = size - 1;
                }
            }
            double notation = Math.pow(10, size);
            result = (size >= 3) ? +(Math.round((number / notation) * 100) / 100.0d) + suffix[(size / 3) - 1] : +number + "";
        }else result = String.valueOf((int)number);
        return result;
    }

    public static String getLvlText(int lvl) {
        ChatColor color = ChatColor.WHITE;
        if (lvl > 10 && lvl <= 25) color = ChatColor.GREEN;
        else if (lvl > 25 && lvl <= 40) color = ChatColor.LIGHT_PURPLE;
        else if (lvl > 40 && lvl <= 60) color = ChatColor.RED;
        else if (lvl > 60 && lvl <= 80) color = ChatColor.DARK_PURPLE;
        else if (lvl > 80) color = ChatColor.DARK_RED;

        return color + String.valueOf(lvl);
    }
}
