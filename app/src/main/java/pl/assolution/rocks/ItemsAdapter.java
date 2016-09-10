package pl.assolution.rocks;

        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

/**
 * Created by Andrzej on 2016-08-30. (ready)
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private SimplyItem.ItemsList list;

    public ItemsAdapter(SimplyItem.ItemsList list) {
        this.list = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        SimplyItem simplyItem  = list.get(position);
        holder.imageView.setImageBitmap(simplyItem.getImage());
        holder.id_rock.setText(simplyItem.getId_rocks());
        holder.designation.setText(simplyItem.getDesignation());
        holder.description.setText(simplyItem.getDescription());
        holder.author.setText(simplyItem.getAuthor());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView id_rock;
        private TextView designation;
        private TextView description;
        private TextView author;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.rock_image);
            id_rock = (TextView) itemView.findViewById(R.id.id_rock_tv);
            designation = (TextView) itemView.findViewById(R.id.designation);
            description = (TextView) itemView.findViewById(R.id.description_content);
            author = (TextView) itemView.findViewById(R.id.author_content);
        }
    }

}
