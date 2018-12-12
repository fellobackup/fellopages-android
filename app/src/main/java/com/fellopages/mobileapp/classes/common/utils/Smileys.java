/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.common.utils;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class Smileys {


    public static final HashMap<String, Integer> SMILEYS_MAP = new LinkedHashMap<>();
    public static final HashMap<String, Character> SMILEYS_CHAR_MAP = new HashMap<>();

    static
    {
        // :-D :D grinning face (U+1F600)
        SMILEYS_MAP.put(":-D", 0x1f600);
        SMILEYS_MAP.put(":D", 0x1f600);

        // =D smiling face with open mouth and smiling eyes (U+1F604)
        SMILEYS_MAP.put("=D", 0x1f604);

        // O:) O:-) O=) Smiling Face With Halo 0x1f607
        SMILEYS_MAP.put("O:)", 0x1f607);
        SMILEYS_MAP.put("O:-)", 0x1f607);
        SMILEYS_MAP.put("O=)", 0x1f607);
        SMILEYS_MAP.put("(A)", 0x1f607); // Angel

        // }:) }:-) }=)	smiling face with horns (U+1F608)
        SMILEYS_MAP.put("}:)", 0x1f608);
        SMILEYS_MAP.put("}:-)", 0x1f608);
        SMILEYS_MAP.put("}=)", 0x1f608);
        SMILEYS_MAP.put(":[", 0x1f608);
        SMILEYS_MAP.put("(6)", 0x1f608);

        // ^:)^ Worship (U+1F47C)
        SMILEYS_MAP.put("^:)^", 0x1f47c);

        // :-) :) :] =)
        SMILEYS_MAP.put(":-)", 0x1f60a);
        SMILEYS_MAP.put(":)", 0x1f60a);
        SMILEYS_MAP.put("=)", 0x1f60a);

        // >.<  >:(  >:-(  >=( Grumpy or Angry face 1F621
        SMILEYS_MAP.put(">.<", 0x1F621);
        SMILEYS_MAP.put(">:(", 0x1F621);
        SMILEYS_MAP.put(">:-(", 0x1F621);
        SMILEYS_MAP.put(">=(", 0x1F621);
        SMILEYS_MAP.put(":@", 0x1F621);

        // :-(|) Monkey (U+1F435)
        SMILEYS_MAP.put(":-(|)", 0x1f435);

        // :-( :( =( sad 0x1f61e
        SMILEYS_MAP.put(":-(", 0x1f61e);
        SMILEYS_MAP.put(":(", 0x1f61e);
        SMILEYS_MAP.put("=(", 0x1f61e);

        // <:o) Party (U+1F389)
        SMILEYS_MAP.put("<:o)", 0x1f389);

        // O.O :O :-O =O surprised 0x1f632
        SMILEYS_MAP.put("O.O", 0x1f632);
        SMILEYS_MAP.put(":O", 0x1f632);
        SMILEYS_MAP.put(":-O", 0x1f632);
        SMILEYS_MAP.put("=O", 0x1f632);
        SMILEYS_MAP.put("|-)", 0x1f632);

        // o.o :o :-o =o face with open mouth (U+1F62E)
        SMILEYS_MAP.put("o.o", 0x1F62E);
        SMILEYS_MAP.put(":o", 0x1F62E);
        SMILEYS_MAP.put(":-o", 0x1F62E);
        SMILEYS_MAP.put("=o", 0x1F62E);

        // ;P ;-P ;p ;-p Tung out 0x1f61c
        SMILEYS_MAP.put(";P", 0x1f61c);
        SMILEYS_MAP.put(";-P", 0x1f61c);
        SMILEYS_MAP.put(";p", 0x1f61c);
        SMILEYS_MAP.put(";-p", 0x1f61c);

        // :P :-P =P :p :-p =p  0X1F61B
        SMILEYS_MAP.put(":P", 0x1F61B);
        SMILEYS_MAP.put(":-P", 0x1F61B);
        SMILEYS_MAP.put(":p", 0x1F61B);
        SMILEYS_MAP.put(":-p", 0x1F61B);
        SMILEYS_MAP.put("=P", 0x1F61B);
        SMILEYS_MAP.put("=p", 0x1F61B);

        // :-* :*  Kissing : 0x1f618
        SMILEYS_MAP.put(":-*", 0x1f617);
        SMILEYS_MAP.put(":*", 0x1f617);

        // ;-)  ;) Winking Face 0x1f609
        SMILEYS_MAP.put(";-)", 0x1f609);
        SMILEYS_MAP.put(";)", 0x1f609);

        // :’( Crying face 0x1f622
        SMILEYS_MAP.put(":'(", 0x1f622);
        SMILEYS_MAP.put("='(", 0x1f622);

        // (y) (Y) Like Button  0x1f44d
        SMILEYS_MAP.put("(y)", 0x1f44d);
        SMILEYS_MAP.put("(Y)", 0x1f44d);

        // (n) (N) UnLike Button  0x1f44e
        SMILEYS_MAP.put("(n)", 0x1f44e);
        SMILEYS_MAP.put("(N)", 0x1f44e);

        // <3 Heart
        SMILEYS_CHAR_MAP.put("(L)", (char) 0x2764);
        SMILEYS_CHAR_MAP.put("<3", (char) 0x2764);

        // <\3 </3 : 0x1F494
        SMILEYS_MAP.put("</3", 0x1F494);
        SMILEYS_MAP.put("(U)", 0x1F494);

        // ^_^;; smiling face with open mouth and cold sweat (U+1F605)
        SMILEYS_MAP.put("^_^;;", 0x1f605);

        // ^_^  :  0X1F601
        SMILEYS_MAP.put("^_^", 0X1F601);

        // (B) Beer (U+1F37A)
        SMILEYS_MAP.put("(B)", 0x1f37a);

        // B) B-) Smiling face with sun glasses :  0X1F60E
        SMILEYS_MAP.put("B)", 0X1F60E);
        SMILEYS_MAP.put("B-)", 0X1F60E);
        SMILEYS_MAP.put("(H)", 0X1F60E);
        SMILEYS_MAP.put("8-|", 0X1F60E);

        // ;* ;-* 0x1f618
        SMILEYS_MAP.put(";*", 0x1f618);
        SMILEYS_MAP.put(";-*", 0x1f618);
        SMILEYS_MAP.put("(K)", 0x1f618);

        // >_< Preserving face 0x1f623
        SMILEYS_MAP.put(">_<", 0x1f623);

        // D: frowning face with open mouth (U+1F626)
        SMILEYS_MAP.put("D:", 0X1F626);

        // -_- expressionless face (U+1F611)
        SMILEYS_MAP.put("-_-", 0X1F611);
        SMILEYS_MAP.put("^o)", 0X1F611);

        // :\ :/ :-\ :-/ =\ =/ confused face (U+1F615)
        SMILEYS_MAP.put(":\\", 0X1F615);
        SMILEYS_MAP.put(":/", 0X1F615);
        SMILEYS_MAP.put(":-\\", 0X1F615);
        SMILEYS_MAP.put(":-/", 0X1F615);
        SMILEYS_MAP.put("=\\", 0X1F615);
        SMILEYS_MAP.put("=/", 0X1F615);
        SMILEYS_MAP.put("*-)", 0X1F615);

        // ~@~
        SMILEYS_MAP.put("~@~", 0x1f4a9);

        // :-| :| =| neutral face (U+1F610)
        SMILEYS_MAP.put(":-|", 0x1f610);
        SMILEYS_MAP.put(":|", 0x1f610);
        SMILEYS_MAP.put("=|", 0x1f610);

        // :S :-S :s :-s confounded face (U+1F616)
        SMILEYS_MAP.put(":S", 0x1f616);
        SMILEYS_MAP.put(":-S", 0x1f616);
        SMILEYS_MAP.put(":s", 0x1f616);
        SMILEYS_MAP.put(":-s", 0x1f616);

        // :X) :3 grinning cat face with smiling eyes
        SMILEYS_MAP.put(":X)", 0x1f638);
        SMILEYS_MAP.put(":3", 0x1f638);
        SMILEYS_MAP.put("(@)", 0x1f638);

        // x_x X-O X-o X( X-( dizzy face (U+1F635)
        SMILEYS_MAP.put("x_x", 0x1f635);
        SMILEYS_MAP.put("X-O", 0x1f635);
        SMILEYS_MAP.put("X-o", 0x1f635);
        SMILEYS_MAP.put("X(", 0x1f635);
        SMILEYS_MAP.put("X-(", 0x1f635);

        // u_u pensive face (U+1F614)
        SMILEYS_MAP.put("u_u", 0x1f614);
        SMILEYS_MAP.put("[-X", 0x1f614);

        // (&) dog face (U+1F436)
        SMILEYS_MAP.put("(&)", 0x1f436);

        // (S) Moon (U+1F319)
        SMILEYS_MAP.put("(S)", 0x1f319);

        // (#) Sun (U+1F315)
        SMILEYS_MAP.put("(#)", 0x1f315);

        // (R) Rainbow (U+1F308)
        SMILEYS_MAP.put("(R)", 0x1f308);

        // (8) Music icon (U+1F3B5)
        SMILEYS_MAP.put("(8)", 0x1f3b5);

        // (~) Film (U+1F3AC)
        SMILEYS_MAP.put("(~)", 0x1f3ac);

        // (8) Rose (U+1F339)
        SMILEYS_MAP.put("(F)", 0x1f339);

        // (O) Clock (U+1F567)
        SMILEYS_MAP.put("(O)", 0x1f567);

        // (G) Gift (U+1F381)
        SMILEYS_MAP.put("(G)",0x1f381);

        // (^) Cake (U+1F382)
        SMILEYS_MAP.put("(^)", 0x1f382);

        // (P) Camera (U+1F4F7)
        SMILEYS_MAP.put("(P)", 0x1f4f7);

        // (I) Lamp (U+1F4A1)
        SMILEYS_MAP.put("(I)", 0x1f4a1);

        // (T) Telephone (U+1F4DE)
        SMILEYS_MAP.put("(T)", 0x1f4de);

        // (mp) Mobile (U+1F4F1)
        SMILEYS_MAP.put("(mp)", 0x1f4f1);

        // (D) Drink (U+1F378)
        SMILEYS_MAP.put("(D)", 0x1f378);

        // :-# Quiet (U+1F636)
        SMILEYS_MAP.put(":-#", 0x1f636);

        // 8o| Teeth (U+1F62C)
        SMILEYS_MAP.put("8o|", 0x1f62c);
        SMILEYS_MAP.put(":^)", 0x1f62c);

        // +o( Sick (U+1F613)
        SMILEYS_MAP.put("+o(", 0x1f613);

        // (tu) Turtle (U+1F422)
        SMILEYS_MAP.put("(tu)", 0x1f422);

        // (sn) Snail (U+1F40C)
        SMILEYS_MAP.put("(sn)", 0x1f40c);

        // (nah) Goat (U+1F41C)
        SMILEYS_MAP.put("(nah)", 0x1f410);

        // (bah) Sheep (U+1F411)
        SMILEYS_MAP.put("(bah)", 0x1f411);

        // (pl) Plate (U+1F35b)
        SMILEYS_MAP.put("(pl)", 0x1f35b);

        // (||) Bowl (U+1F35c)
        SMILEYS_MAP.put("(||)", 0x1f35c);

        // (pi) Pizza (U+1F355)
        SMILEYS_MAP.put("(pi)", 0x1f355);

        // (au) Car (U+1F697)
        SMILEYS_MAP.put("(au)", 0x1f697);

        // (ip) Island (U+1F391)
        SMILEYS_MAP.put("(ip)", 0x1f391);

        // (co) Coins (U+1F4B0)
        SMILEYS_MAP.put("(co)", 0x1f4b0);

        // :$ Embarrassed face (U+1F633)
        SMILEYS_MAP.put(":$", 0x1f633);

        // I-) Sleepy (U+1F634)
        SMILEYS_MAP.put("I-)", 0x1f634);

        // :-W Waiting (U+1F647)
        SMILEYS_MAP.put(":-W", 0x1f647);

        // =; Bye (U+1F44B)
        SMILEYS_MAP.put("=;", 0x1f44b);

        // :-X Shut mouth (U+1F637)
        SMILEYS_MAP.put(":-X", 0x1f637);

        // (Z) Boy (U+1F466)
        SMILEYS_MAP.put("(Z)", 0x1f466);

        // (X) Girl (U+1F467)
        SMILEYS_MAP.put("(X)", 0x1f467);

        // (brb) Brb (U+1F519)
        SMILEYS_MAP.put("(brb)", 0x1f519);

        // 8-) Eyeroll (U+1F612)
        SMILEYS_MAP.put("8-)", 0x1f612);

        // (}) Hug (U+1F450)
        SMILEYS_MAP.put("(})", 0x1f450);

        // ({) Hug (U+1F64C)
        SMILEYS_MAP.put("({)", 0x1f64c);

        // (E) Mail
        SMILEYS_CHAR_MAP.put("(E)", (char) 0x2709);

        // (*) Star
        SMILEYS_CHAR_MAP.put("(*)", (char) 0x2b50);

        // (C) Coffee
        SMILEYS_CHAR_MAP.put("(C)", (char) 0x2615);

        // (so) Soccerball
        SMILEYS_CHAR_MAP.put("(so)", (char) 0x26bd);

        // (um) Umbrella
        SMILEYS_CHAR_MAP.put("(um)", (char) 0x2614);

        // (st) Rain
        SMILEYS_CHAR_MAP.put("(st)", (char) 0x2601);

        // (li) Thunder
        SMILEYS_CHAR_MAP.put("(li)", (char) 0x26a1);

        // (ap) Airplane
        SMILEYS_CHAR_MAP.put("(ap)", (char) 0x2708);

        // (h5) High five
        SMILEYS_CHAR_MAP.put("(h5)", (char) 0x270b);
    }

    /**
     *
     * Method to return string with emoji icons.
     * @param body body with symbol
     * @return returns the string with emoji icons.
     */
    public static String getEmojiFromString(String body) {

        Set<String> set = SMILEYS_MAP.keySet();
        String[] parts = body.split(" ");
        for (String s : set) {
            Emojicon emojicon = Emojicon.fromCodePoint(SMILEYS_MAP.get(s));
            if(body.contains(s)){
                // Check if string contains
                for (int i = 0; i < parts.length; i ++) {
                    if (!(parts[i].contains("https://") || parts[i].contains("http://"))) {
                        parts[i] = parts[i].replace(s, emojicon.getEmoji());
                    }
                }
            }
        }

        body = TextUtils.join(" ", parts);

        Set<String> setCharMap = SMILEYS_CHAR_MAP.keySet();
        for (String s : setCharMap) {
            if(body.contains(s)){
                Emojicon emojicon = Emojicon.fromChar(SMILEYS_CHAR_MAP.get(s));
                body = body.replace(s, emojicon.getEmoji());
            }
        }
        return body;
    }
}
