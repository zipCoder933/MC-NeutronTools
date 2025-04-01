package me.hypherionmc.morecreativetabs.client.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
public class DisabledTabsJsonHelper {

    @SerializedName("disabled_tabs")
    private ArrayList<String> disabledTabs;

    public ArrayList<String> getDisabledTabs(){
        return disabledTabs;
    }
}
