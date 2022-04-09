package edu.byu.cs240.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {
    private TextView firstname;
    private TextView lastname;
    private TextView gender;
    private ExpandableListView eventList;
    private ExpandableListView familyList;

    private static final String FEMALE = "Female";
    private static final String MALE = "MALE";

    public static final String PERSON_ID_KEY = "personID";

    private PersonActivityViewModel getPersonActivityViewModel() {
        return ViewModelProviders.of(this).get(PersonActivityViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        getPersonActivityViewModel().setPersonData(intent);

        firstname = findViewById(R.id.person_firstname);
        firstname.setText(getPersonActivityViewModel().getCurrPerson().getFirstName());

        lastname = findViewById(R.id.person_lastname);
        lastname.setText(getPersonActivityViewModel().getCurrPerson().getLastName());

        gender = findViewById(R.id.person_gender);
        gender.setText(getGenderString());

        eventList = findViewById(R.id.expandableList);

        eventList.setAdapter(new ExpandableListAdapter(getPersonActivityViewModel().getFilteredCurrPersonEvents(),
                getPersonActivityViewModel().getCurrPersonFamily()));

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> lifeEvents;
        private final List<Person> family;

        ExpandableListAdapter(List<Event> lifeEvents, List<Person> family) {
            this.lifeEvents = lifeEvents;
            this.family = family;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return lifeEvents.size();
                case FAMILY_GROUP_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return getString(R.string.lifeEventsTitle);
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.familyTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return lifeEvents.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return family.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.lifeEventsTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            TextView eventNameView = eventItemView.findViewById(R.id.textView4);
            eventNameView.setText(getEventString(lifeEvents.get(childPosition)));

            TextView nameView = eventItemView.findViewById(R.id.textView5);
            nameView.setText(getNameString(lifeEvents.get(childPosition)));

            ImageView eventImageView = eventItemView.findViewById(R.id.imageView2);
            Drawable locationIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.grey).sizeDp(40);

            eventImageView.setImageDrawable(locationIcon);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToMapActivity(lifeEvents.get(childPosition));
                }
            });
        }

        private void initializeFamilyView(View itemView, final int childPosition) {
            TextView personNameView = itemView.findViewById(R.id.textView4);
            personNameView.setText(getNameString(family.get(childPosition)));

            TextView relationView = itemView.findViewById(R.id.textView5);
            relationView.setText(getPersonActivityViewModel()
                    .getRelation(family.get(childPosition).getPersonID()));

            ImageView genderImageView = itemView.findViewById(R.id.imageView2);

            char gender = family.get(childPosition).getGender();
            Drawable genderIcon;
            if(gender == 'm'){
                genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male)
                        .colorRes(R.color.male_icon).sizeDp(40);
            } else {
                genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female)
                        .colorRes(R.color.female_icon).sizeDp(40);
            }
            genderImageView.setImageDrawable(genderIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToPersonActivity(family.get(childPosition));
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void switchToPersonActivity(Person person) {
        Intent intent = PersonActivity.newIntent(this);
        String personID = person.getPersonID();
        intent.putExtra(PERSON_ID_KEY, personID);
        startActivity(intent);
    }

    private void switchToMapActivity(Event event) {
        DataCache.getInstance().setStartEvent(event);
        Intent intent = EventActivity.newIntent(this);
        startActivity(intent);
    }



    private String getEventString(Event event) {
        return event.getEventType().toUpperCase() + ": " + event.getCity() +
                ", " + event.getCountry() + " (" + event.getYear() + ")";
    }

    private String getNameString(Person person){
        return person.getFirstName() + " " + person.getLastName();
    }

    private String getNameString(Event event){
        Person associatedPerson = DataCache.getInstance().getPerson(event.getPersonID());
        return associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
    }


    private String getGenderString() {
        char gender = getPersonActivityViewModel().getCurrPerson().getGender();
        if(gender == 'm'){
            return MALE;
        } else {
            return FEMALE;
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, PersonActivity.class);
        return intent;
    }
}