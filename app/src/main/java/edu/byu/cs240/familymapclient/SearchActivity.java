package edu.byu.cs240.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {
    private static String TAG = "SearchActivity";

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private SearchView searchView;
    private List<Event> events;
    private List<Person> people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        List<Event> events = new ArrayList<>();
        List<Person> people = new ArrayList<>();

        searchView = findViewById(R.id.searchView);

        SearchAdapter adapter = new SearchAdapter(people, events);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SearchActivity.class);
        return intent;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<Person> people;
        private List<Event> events;


        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            view = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }

        public void filter(String text) {
            List<Person> allPeople = DataCache.getInstance().getPersonList();
            List<Event> allEvents = DataCache.getInstance().getEventList();

            people.clear();
            events.clear();

            if(text != null || !text.equals("")){
                for(Person person : allPeople) {
                    if(fitsPersonFilter(text, person)){
                        people.add(person);
                    }
                }
                for(Event event : allEvents) {
                    if(fitsEventFilter(text, event)){
                        events.add(event);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private boolean fitsPersonFilter(String text, Person person){
        text = text.toLowerCase();
        String firstname = person.getFirstName().toLowerCase();
        String lastname = person.getLastName().toLowerCase();

        if(lastname.contains(text) || firstname.contains(text)){
            return true;
        }
        return false;
    }

    private boolean fitsEventFilter(String text, Event event){
        text = text.toLowerCase();
        String country = event.getCountry().toLowerCase();
        String city = event.getCity().toLowerCase();
        String eventType = event.getEventType().toLowerCase();
        String year = Integer.toString(event.getYear());

        if(!DataCache.getInstance().checkSettingCompliance(event)){
            return false;
        }

        if(country.contains(text) || city.contains(text) || eventType.contains(text) || year.contains(text)){
            return true;
        }
        return false;
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView topLine;
        private final TextView bottomLine;
        private final ImageView image;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            topLine = itemView.findViewById(R.id.textView4);

            bottomLine = itemView.findViewById(R.id.textView5);

            image = itemView.findViewById(R.id.imageView2);
        }

        private void bind(Person person) {
            this.person = person;
            topLine.setText(getNameString(person));

            image.setImageDrawable(getGenderDrawable(person.getGender()));
        }

        private void bind(Event event) {
            this.event = event;
            topLine.setText(getEventString(event));
            bottomLine.setText(getNameString(DataCache.getInstance()
                    .getAssociatedPerson(event.getEventID())));

            Drawable locationIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.grey).sizeDp(40);

            image.setImageDrawable(locationIcon);
        }

        @Override
        public void onClick(View view) {
            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                switchToPersonActivity(person);
            } else {
                switchToEventActivity(event);
            }
        }

    }

    private void switchToPersonActivity(Person person) {
        Intent intent = PersonActivity.newIntent(this);
        String personID = person.getPersonID();
        intent.putExtra(PersonActivity.PERSON_ID_KEY, personID);
        startActivity(intent);
    }

    private void switchToEventActivity(Event event) {
        DataCache.getInstance().setStartEvent(event);
        Intent intent = EventActivity.newIntent(this);
        startActivity(intent);
    }

    private Drawable getGenderDrawable(char gender){
        if(gender =='m'){
            return new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male)
                    .colorRes(R.color.male_icon).sizeDp(40);
        } else {
            return new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female)
                    .colorRes(R.color.female_icon).sizeDp(40);
        }
    }

    private String getNameString(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }

    private String getEventString(Event event) {
        return event.getEventType().toUpperCase() + ": " + event.getCity() +
                ", " + event.getCountry() + " (" + event.getYear() + ")";
    }
}