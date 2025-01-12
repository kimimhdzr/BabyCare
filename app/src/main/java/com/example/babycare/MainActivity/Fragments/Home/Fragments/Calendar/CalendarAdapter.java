package com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Constants for view types
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_EVENT = 1;
    private List<Object> groupedItems; // Mixed list of date strings and events
    private OnEventClickListener eventClickListener;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

    // Interface for handling click events
    public interface OnEventClickListener {
        void onEventClick(CalendarClass event);
    }

    // Constructor to initialize the adapter
    public CalendarAdapter(List<CalendarClass> calendarItems) {
        this.groupedItems = new ArrayList<>();
        groupEventsByDate(calendarItems);
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.eventClickListener = listener;
    }

    // Method to group events by date
    private void groupEventsByDate(List<CalendarClass> calendarList) {
        Map<String, List<CalendarClass>> groupedMap = new LinkedHashMap<>();

        // Group events by date
        for (CalendarClass event : calendarList) {
            groupedMap.computeIfAbsent(event.getDate(), k -> new ArrayList<>()).add(event);
        }

        // Flatten the grouped data into a single list
        groupedItems.clear(); // Clear existing items before adding new ones
        for (Map.Entry<String, List<CalendarClass>> entry : groupedMap.entrySet()) {
            groupedItems.add(entry.getKey()); // Add the date as a header
            groupedItems.addAll(entry.getValue()); // Add all events under the date
        }
    }

    // Method to add a new event and refresh the adapter
    public void addEvent(CalendarClass newEvent) {
        String eventDate = newEvent.getDate();
        boolean dateExists = false;
        int insertPosition = -1;

        try {
            Date newEventDate = DATE_FORMAT.parse(eventDate);

            // Find the correct position to insert the event
            for (int i = 0; i < groupedItems.size(); i++) {
                if (groupedItems.get(i) instanceof String) {
                    String currentDateStr = (String) groupedItems.get(i);
                    Date currentDate = DATE_FORMAT.parse(currentDateStr);

                    if (currentDate.equals(newEventDate)) {
                        dateExists = true;
                        insertPosition = i + 1; // Position to insert under the date header
                        break;
                    } else if (newEventDate.before(currentDate)) {
                        insertPosition = i; // Position to insert the new date header
                        break;
                    }
                }
            }

            if (dateExists) {
                // Insert event under the existing date header while maintaining order by start time
                for (int i = insertPosition; i < groupedItems.size(); i++) {
                    if (groupedItems.get(i) instanceof CalendarClass) {
                        CalendarClass existingEvent = (CalendarClass) groupedItems.get(i);
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

                // Append to the end of this date's events if no earlier position is found
                groupedItems.add(insertPosition, newEvent);
            } else {
                // Insert the new date header and the event
                if (insertPosition == -1) {
                    // No later date exists, so append at the end
                    groupedItems.add(eventDate);
                    groupedItems.add(newEvent);
                } else {
                    // Insert the date header and event at the determined position
                    groupedItems.add(insertPosition, eventDate);
                    groupedItems.add(insertPosition + 1, newEvent);
                }
            }

            notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // **Update: Enhanced removeEvent logic to handle date header removal**
    public void removeEvent(CalendarClass event) {
        String eventDate = event.getDate();
        groupedItems.remove(event);

        // Check if any other event exists for the same date
        boolean hasOtherEvents = false;
        for (Object item : groupedItems) {
            if (item instanceof CalendarClass && ((CalendarClass) item).getDate().equals(eventDate)) {
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
            CalendarClass event = (CalendarClass) groupedItems.get(position);
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
