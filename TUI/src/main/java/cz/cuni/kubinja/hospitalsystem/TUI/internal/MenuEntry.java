package cz.cuni.kubinja.hospitalsystem.TUI.internal;

import org.jetbrains.annotations.NotNull;

/**
 * Represents data mapping one menu option to the associated function.
 *
 * @param text Text displayed in the menu.
 * @param method Method that will be runed every time the option is selected.
 */
record MenuEntry(String text, Runnable method){
    @Override
    @NotNull
    public String toString(){
        return text;
    }
}
