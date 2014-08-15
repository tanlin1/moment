package content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.moment.R;

import java.util.List;

/**
 * Created by Administrator on 2014/8/12.
 */
public class TestAdapter extends BaseAdapter {
	private List<Person> list;
	private LayoutInflater layout;

	public TestAdapter(Context context, List<Person> list) {
		this.list = list;
		layout = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){
			vh = new ViewHolder();
			convertView = layout.inflate(R.layout.notify,null);

			vh.id = (TextView) convertView.findViewById(R.id.id);
			vh.name = (TextView) convertView.findViewById(R.id.name);
			vh.phoneNumber = (TextView) convertView.findViewById(R.id.number);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}

		Person p = list.get(position);

		vh.id.setText(p.getId());

		vh.name.setText(p.getName());
		vh.phoneNumber.setText(p.getNum());

		return convertView;
	}
	private final class ViewHolder {
		TextView id;
		TextView name;
		TextView phoneNumber;
	}
}
