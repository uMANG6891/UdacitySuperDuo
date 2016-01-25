package it.jaschke.alexandria.ui.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.utility.Constants;
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
            eanBookId = arguments.getString(Constants.EAN_BOOK_ID);
            eanIsTablet = arguments.getBoolean(Constants.EAN_IS_TABLET);
        }
        getLoaderManager().initLoader(LOADER_ID, null, this);
        rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
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
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanBookId)),
                        null,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
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
                if (authors != null) {
                    String[] authorsArr = authors.split(",");
                    tvAuthors.setLines(authorsArr.length);
                    tvAuthors.setText(authors.replace(",", "\n"));
                }
                String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
                Ion.with(this)
                        .load(imgUrl)
                        .withBitmap()
                        .placeholder(R.drawable.book_default)
                        .error(R.drawable.book_default)
                        .intoImageView(ivFullBookCover);

                String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
                tvCategories.setText(categories);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_button:
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(getContext());
                builderDelete.setTitle(R.string.dialog_title_delete)
                        .setMessage(R.string.dialog_message_delete)
                        .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Utility.deleteBook(getContext(), eanBookId);
                                if (eanIsTablet) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    getActivity().finish();
                                }
                                Utility.deleteBook(getContext(), eanBookId);
                                Toast.makeText(getContext(),
                                        getString(R.string.book_deleted, bookTitle),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.dialog_button_dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            default:
                break;
        }
    }
}