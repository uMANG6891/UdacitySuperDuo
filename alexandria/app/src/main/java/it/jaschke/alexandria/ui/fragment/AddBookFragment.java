package it.jaschke.alexandria.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.koushikdutta.ion.Ion;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    @Bind(R.id.bookTitle)
    TextView tvBookTitle;
    @Bind(R.id.bookSubTitle)
    TextView tvBookSubTitle;
    @Bind(R.id.authors)
    TextView tvAuthors;
    @Bind(R.id.categories)
    TextView tvCategories;

    @Bind(R.id.ean)
    EditText etEan;

    @Bind(R.id.bookCover)
    ImageView ivBookCover;

    @Bind(R.id.scan_button)
    Button bScan;
    @Bind(R.id.save_button)
    Button bSave;
    @Bind(R.id.delete_button)
    Button bDelete;

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT = "eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";


    public AddBookFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etEan != null) {
            outState.putString(EAN_CONTENT, etEan.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);

        etEan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ISBNNumber = s.toString();
                if (ISBNNumber.length() == 10 && !ISBNNumber.startsWith("978")) {
                    ISBNNumber = "978" + ISBNNumber;
                }
                if (ISBNNumber.length() < 13) {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ISBNNumber);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBookFragment.this.restartLoader();
            }
        });
        bScan.setOnClickListener(this);
        bSave.setOnClickListener(this);
        bDelete.setOnClickListener(this);

        if (savedInstanceState != null) {
            etEan.setText(savedInstanceState.getString(EAN_CONTENT));
            etEan.setHint("");
        }

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            etEan.setText(scanResult.getContents());
        }
        // else continue with any other code you need in the method
    }

    private void restartLoader() {
        getLoaderManager().destroyLoader(LOADER_ID);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (etEan.getText().length() == 0) {
            return null;
        }
        String eanStr = etEan.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        tvBookTitle.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        tvBookSubTitle.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        tvAuthors.setLines(authorsArr.length);
        tvAuthors.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            ivBookCover.setVisibility(View.VISIBLE);
            Ion.with(this)
                    .load(imgUrl)
                    .withBitmap()
                    .placeholder(R.drawable.image_loading)
                    .error(R.drawable.image_error)
                    .intoImageView(ivBookCover);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        tvCategories.setText(categories);

        bSave.setVisibility(View.VISIBLE);
        bDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields() {
        tvBookTitle.setText("");
        tvBookSubTitle.setText("");
        tvAuthors.setText("");
        tvCategories.setText("");
        ivBookCover.setVisibility(View.INVISIBLE);
        bSave.setVisibility(View.INVISIBLE);
        bDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.menu_scan);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_button:
                FragmentIntentIntegrator integrator = new FragmentIntentIntegrator(AddBookFragment.this);
                integrator.initiateScan();
                break;
            case R.id.save_button:
                etEan.setText("");
                break;
            case R.id.delete_button:
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, etEan.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                etEan.setText("");
                break;
            default:
                break;
        }
    }

    public final class FragmentIntentIntegrator extends IntentIntegrator {
        private final Fragment fragment;

        public FragmentIntentIntegrator(Fragment fragment) {
            super(fragment.getActivity());
            this.fragment = fragment;
        }

        @Override
        protected void startActivityForResult(Intent intent, int code) {
            fragment.startActivityForResult(intent, code);
        }
    }
}
