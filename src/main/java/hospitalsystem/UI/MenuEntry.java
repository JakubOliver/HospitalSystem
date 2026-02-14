package hospitalsystem.UI;

import org.jetbrains.annotations.NotNull;

record MenuEntry(String text, Runnable method){
    @Override
    @NotNull
    public String toString(){
        return text;
    }
}
