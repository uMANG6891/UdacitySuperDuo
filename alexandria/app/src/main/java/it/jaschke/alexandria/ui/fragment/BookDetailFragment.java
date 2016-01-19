package it.jaschke.alexandria.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.ui.activity.BookDetailActivity;
import it.jaschke.alexandria.ui.activity.MainActivity;
import it.jaschke.alexandria.utility.Utility;


public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    @Bind(R.id.fullBookTitle)
    TextView tvFullBookTitle;
    @Bind(R.id.fullBookSubTitle)
    TextView tvFullBookSubTitle;
    @Bind(R.id.fullBookDesc)
    TextView tvDescription;
    @Bind(R.id.authors)
    TextView tvAuthors;
    @Bind(R.id.categories)
    TextView tvCategories;

    @Bind(R.id.fullBookCover)
    ImageView ivFullBookCover;

    @Bind(R.id.delete_button)
    Button bDelete;

    private final int LOADER_ID = 10;
    private View rootView;

    private String eanBookId;
    private boolean eanIsTablet;

    private String bookTitle;

    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            eanBookId = arguments.getString(BookDetailActivity.EAN_BOOK_ID);
            eanIsTablet = arguments.getBoolean(BookDetailActivity.EAN_IS_TABLET);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        ButterKnife.bind(this, rootView);
        bDelete.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_action_share:
                Utility.shareBook(getActivity(), bookTitle);
                return true;
            default:
                return false;
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanBookId)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        tvFullBookTitle.setText(bookTitle);
        if (!eanIsTablet) {
            getActivity().setTitle(bookTitle);
        }

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        tvFullBookSubTitle.setText(bookSubTitle);

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        tvDescription.setText(desc);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        tvAuthors.setLines(authorsArr.length);
        tvAuthors.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            Glide.with(this)
                    .load(imgUrl)
                    .into(ivFullBookCover);
            ivFullBookCover.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        tvCategories.setText(categories);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if (MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container) == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_button:
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, eanBookId);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                if (eanIsTablet)
                    getActivity().getSupportFragmentManager().popBackStack();
                else
                    getActivity().finish();
                break;
            default:
                break;
        }
    }
}