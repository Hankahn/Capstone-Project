package com.essentialtcg.magicthemanaging;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shawn on 3/21/2016.
 */
public class CardUtil {

    public static int parseSetRarity(String setCode, String rarity) {
        String resId = String.format("%s_%s", setCode.toLowerCase(),
                rarity.toLowerCase().replace(" ", ""));

        return Util.getResId(resId, R.mipmap.class);
    }

    public static ArrayList<Integer> parseIcons(String text) {
        ArrayList<Integer> iconsIds = new ArrayList<>();

        Pattern iconFinder = Pattern.compile("\\{(.*?)\\}");
        Matcher iconMatcher = iconFinder.matcher(text);

        while(iconMatcher.find()) {
            switch (iconMatcher.group(1).toLowerCase()) {
                case "w":
                    iconsIds.add(R.drawable.mana_white);
                    break;
                case"u":
                    iconsIds.add(R.drawable.mana_blue);
                    break;
                case "b":
                    iconsIds.add(R.drawable.mana_black);
                    break;
                case "r":
                    iconsIds.add(R.drawable.mana_red);
                    break;
                case "g":
                    iconsIds.add(R.drawable.mana_green);
                    break;
                case "c":
                    iconsIds.add(R.drawable.mana_colorless);
                    break;
                case "x":
                    iconsIds.add(R.drawable.mana_x);
                    break;
                case "y":
                    iconsIds.add(R.drawable.mana_y);
                    break;
                case "z":
                    iconsIds.add(R.drawable.mana_z);
                    break;
                case "w/u":
                case "u/w":
                    iconsIds.add(R.drawable.mana_white_blue);
                    break;
                case "w/b":
                case "b/w":
                    iconsIds.add(R.drawable.mana_white_black);
                    break;
                case "w/r":
                case "r/w":
                    iconsIds.add(R.drawable.mana_white_red);
                    break;
                case "w/g":
                case "g/w":
                    iconsIds.add(R.drawable.mana_white_green);
                    break;
                case "u/b":
                case "b/u":
                    iconsIds.add(R.drawable.mana_blue_black);
                    break;
                case "u/r":
                case "r/u":
                    iconsIds.add(R.drawable.mana_blue_red);
                    break;
                case "u/g":
                case "g/u":
                    iconsIds.add(R.drawable.mana_blue_green);
                    break;
                case "b/r":
                case "r/b":
                    iconsIds.add(R.drawable.mana_black_red);
                    break;
                case "b/g":
                case "g/b":
                    iconsIds.add(R.drawable.mana_black_green);
                    break;
                case "r/g":
                case "g/r":
                    iconsIds.add(R.drawable.mana_red_green);
                    break;
                case "w/p":
                case "p/w":
                    iconsIds.add(R.drawable.mana_white_phyrexian);
                    break;
                case "u/p":
                case "p/u":
                    iconsIds.add(R.drawable.mana_blue_phyrexian);
                    break;
                case "b/p":
                case "p/b":
                    iconsIds.add(R.drawable.mana_black_phyrexian);
                    break;
                case "r/p":
                case "p/r":
                    iconsIds.add(R.drawable.mana_red_phyrexian);
                    break;
                case "g/p":
                case "p/g":
                    iconsIds.add(R.drawable.mana_green_phyrexian);
                    break;
                case "2/w":
                case "w/2":
                    iconsIds.add(R.drawable.mana_2_white);
                    break;
                case "2/u":
                case "u/2":
                    iconsIds.add(R.drawable.mana_2_blue);
                    break;
                case "2/b":
                case "b/2":
                    iconsIds.add(R.drawable.mana_2_black);
                    break;
                case "2/r":
                case "r/2":
                    iconsIds.add(R.drawable.mana_2_red);
                    break;
                case "2/g":
                    iconsIds.add(R.drawable.mana_2_green);
                    break;
                default:
                    iconsIds.add(Util.getResId(
                            String.format("mana_%s", iconMatcher.group(1).toLowerCase()),
                            R.drawable.class));
                    Log.d("MtM", String.format("mana_%s", iconMatcher.group(1).toLowerCase()));
            }
        }

        return iconsIds;
    }

}
