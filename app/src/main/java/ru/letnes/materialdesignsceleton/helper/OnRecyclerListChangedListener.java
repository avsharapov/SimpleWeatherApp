package ru.letnes.materialdesignsceleton.helper;


import java.util.List;
import ru.letnes.materialdesignsceleton.model.WeatherData;

public interface OnRecyclerListChangedListener {

    public void onNoteListChanged(List<WeatherData> weatherData);

}
