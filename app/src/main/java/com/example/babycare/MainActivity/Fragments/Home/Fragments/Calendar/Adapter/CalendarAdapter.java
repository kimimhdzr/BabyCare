package com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babycare.DataBinding.Model.CalendarModel;
import com.example.babycare.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    // Constants for view types
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_EVENT = 1;
    private List<Object> groupedItems; // Mixed list of date strings and events
    private OnEventClickListener eventClickListener;

    // Interface for handling click events
    public interface OnEventClickListener {
        void onEventClick(CalendarModel event);
    }

    // Constructor to initialize the adapter
    public CalendarAdapter(List<CalendarModel> calendarItems) {
        this.groupedItems = new ArrayList<>();
        groupEventsByDate(calendarItems);
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.eventClickListener = listener;
    }

    // Method to group events by date
    private void groupEventsByDate(List<CalendarModel> calendarList) {
        Map<String, List<CalendarModel>> groupedMap = new LinkedHashMap<>();

        // Group events by date
        for (CalendarModel event : calendarList) {
            groupedMap.computeIfAbsent(event.getDate(), k -> new ArrayList<>()).add(event);
        }

        // Flatten the grouped data into a single list
        groupedItems.clear(); // Clear existing items before adding new ones
        for (Map.Entry<String, List<CalendarModel>> entry : groupedMap.entrySet()) {
            groupedItems.add(entry.getKey()); // Add the date as a header
            groupedItems.addAll(entry.getValue()); // Add all events under the date
        }
    }

    // Method to add a new event and refresh the adapter
    public void addEvent(CalendarModel newEvent) {
        String eventDate = newEvent.getDate();
        boolean dateExists = false;
        int insertPosition = -1;

        // Find the position of the date header and check if it exists
        for (int i = 0; i < groupedItems.size(); i++) {
            if (groupedItems.get(i) instanceof String && groupedItems.get(i).equals(eventDate)) {
                dateExists = true;
                insertPosition = i + 1; // Set position to insert event right after the date header
                break;
            }
        }

        if (dateExists) {
            // Find the correct position to insert while maintaining sorted order (optional)
            for (int i = insertPosition; i < groupedItems.size(); i++) {
                if (groupedItems.get(i) instanceof CalendarModel) {
                    CalendarModel existingEvent = (CalendarModel) groupedItems.get(i);
                    if (existingEvent.getDate().equals(eventDate) &&
                            newEvent.getStartTime().compareTo(existingEvent.getStartTime()) < 0) {
                        groupedItems.add(i, newEvent);
                        notifyDataSetChanged();
                        return;
                    }
                } else {
                    break; // Reached the next date header
                }
            }

            // If not inserted in the loop, append to the end of this date's events
            groupedItems.add(insertPosition, newEvent);
        } else {
            // If the date header doesn't exist, add the date and the event
            groupedItems.add(eventDate); // Add date header
            groupedItems.add(newEvent);
        }

        notifyDataSetChanged();
    }

    // **Update: Enhanced removeEvent logic to handle date header removal**
    public void removeEvent(CalendarModel event) {
        String eventDate = event.getDate();
        groupedItems.remove(event);

        // Check if any other event exists for the same date
        boolean hasOtherEvents = false;
        for (Object item : groupedItems) {
            if (item instanceof CalendarModel && ((CalendarModel) item).getDate().equals(eventDate)) {
                hasOtherEvents = true;
                break;
            }
        }

        // Remove the date header if no other events exist for this date
        if (!hasOtherEvents) {
            groupedItems.remove(eventDate);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return groupedItems.get(position) instanceof String ? VIEW_TYPE_DATE : VIEW_TYPE_EVENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_calendar_item, parent, false);
            return new EventViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateViewHolder) {
            String date = (String) groupedItems.get(position);
            ((DateViewHolder) holder).dateTextView.setText(date);
        } else if (holder instanceof EventViewHolder) {
            CalendarModel event = (CalendarModel) groupedItems.get(position);
            ((EventViewHolder) holder).titleTextView.setText(event.getTitle());
            String timeRange = event.getStartTime() + " - " + event.getEndTime();
            ((EventViewHolder) holder).timeRangeTextView.setText(timeRange);

            holder.itemView.setOnClickListener(v -> {
                if (eventClickListener != null) {
                    eventClickListener.onEventClick(event);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groupedItems.size();
    }

    // ViewHolder for date header
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.TVDateHeader);
        }
    }

    // ViewHolder for event item
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeRangeTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.TVTitle);
            timeRangeTextView = itemView.findViewById(R.id.TVTimeRange);
        }
    }
}
