package adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mappedvision.shortcuts.net.R;
import model.Item;
import utils.WebsiteRecentSearch;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {
    private List<Item> suggestions;
    private Context context;
    Activity activity;

    public SuggestionsAdapter(List<Item> suggestions, Context context, Activity activity) {
        this.suggestions = suggestions;
        this.context = context;
        this.activity = activity;
    }

    public void updateSuggestions(List<Item> newSuggestions) {
        suggestions.clear();
        suggestions.addAll(newSuggestions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item suggestion = suggestions.get(position);
        holder.bind(suggestion);
        if (position == getItemCount()-1){
            holder.stroke.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(suggestions.size(), 5);
    }

    private void hideKeyboard() {
        // Get the input method manager
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if no view has focus
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(context);
        }

        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSuggestion;
        View stroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSuggestion = itemView.findViewById(R.id.textViewSuggestion);
            stroke = itemView.findViewById(R.id.stroke);

            // Add click listener to the itemView
            itemView.setOnClickListener(v -> {
                hideKeyboard();
                // Handle click event to open the link
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Item suggestion = suggestions.get(position);
                    openLink(suggestion.getLink());
                }
            });
        }

        public void bind(Item suggestion) {
            textViewSuggestion.setText(suggestion.getTitle());
        }

        private void openLink(String link) {
            try {
                if (!WebsiteRecentSearch.getStringArray(context).contains(link)){
                    WebsiteRecentSearch.addItem(context, link);
                }

                // Create an intent to open the link in a web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
                hideKeyboard();
            } catch (ActivityNotFoundException e) {
                // Handle the case where no activity is found to handle the intent
                showToast("No browser found to open");
                hideKeyboard();
            } catch (Exception e) {
                // Handle other exceptions
                showToast("Error occurred while opening the link");
                hideKeyboard();
                e.printStackTrace();
            }
        }

    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
        toast.show();
    }
}
