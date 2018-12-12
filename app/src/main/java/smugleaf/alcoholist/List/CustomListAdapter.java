package smugleaf.alcoholist.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smugleaf.alcoholist.R;

public class CustomListAdapter extends ArrayAdapter<ListItem> {

    private Context context;
    private ArrayList<ListItem> list;// = new ArrayList<>();

    public CustomListAdapter(Context context, ArrayList<ListItem> list) {
        super(context, 0, list);
        this.context = context;
//        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public ListItem getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        ListItem item = getItem(position);
        return item.getTitle().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
//            listItem = activity.getLayoutInflater().inflate(R.layout.list_item, null);
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        ListItem item = list.get(position);

        ImageView imageView = (ImageView) listItem.findViewById(R.id.item_icon);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().getIdentifier(item.getIcon(), "drawable", context.getPackageName())));
//        imageView.setImageResource(activity.getResources().getIdentifier(item.getIcon(), "string", activity.getPackageName()));

        TextView title = (TextView) listItem.findViewById(R.id.item_title);
        title.setText(item.getTitle());

        return listItem;
    }
}