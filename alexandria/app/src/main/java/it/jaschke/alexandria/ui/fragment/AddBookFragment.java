package it.jaschke.alexandria.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.FetchBookInfo;
import it.jaschke.alexandria.utility.Constants;
import it.jaschke.alexandria.utility.Utility;


public class AddBookFragment extends Fragment implements View.OnClickListener {

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
    @Bind(R.id.clear_button)
    Button bClear;

    @Bind(R.id.frag_ab_ll_book_detail)
    LinearLayout llBookDetail;

    private final String EAN_CONTENT = "eanContent";

    ArrayMap<String, String> BOOK_INFO = null;

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

        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);
        hideBookInfo();

        etEan.addTextChangedListener(
                new TextWatcher() {
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
                            hideBookInfo();
                            return;
                        }
                        //Once we have an ISBN, get book info
                        FetchBookInfo.load(getContext(), ISBNNumber, new FetchBookInfo.GetBookInfo() {
                                    @Override
                                    public void onComplete(ArrayMap<String, String> map) {
                                        if (map == null || map.keySet().size() <= 1) {
                                            hideBookInfo();
                                            showErrorMessage();
                                        } else {
                                            BOOK_INFO = map;
                                            showBookInfoOnUI();
                                        }
                                    }
                                }
                        );
                    }
                }

        );
        bScan.setOnClickListener(this);
        bSave.setOnClickListener(this);
        bClear.setOnClickListener(this);

        if (savedInstanceState != null) {
            etEan.setText(savedInstanceState.getString(EAN_CONTENT));
            hideBookInfo();
        }

        return rootView;
    }

    private void hideBookInfo() {
        llBookDetail.setVisibility(View.GONE);
    }

    private void showBookInfo() {
        llBookDetail.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        int message;
        if (!Utility.isNetworkAvailable(getContext())) {
            message = R.string.error_no_internet_access;
        } else
            switch (Utility.getSearchStatus(getContext())) {
                case FetchBookInfo.SEARCH_STATUS_OK:
                    message = -1;
                    break;
                case FetchBookInfo.SEARCH_STATUS_OK_EMPTY:
                    message = R.string.error_ok_empty;
                    break;
                case FetchBookInfo.SEARCH_STATUS_SERVER_DOWN:
                    message = R.string.error_server_down;
                    break;
                case FetchBookInfo.SEARCH_STATUS_SERVER_INVALID:
                    message = R.string.error_server_invalid;
                    break;
                default:
                case FetchBookInfo.SEARCH_STATUS_UNKNOWN:
                    message = R.string.error_server_unknown;
                    break;
            }
        if (message != -1) {
            Toast.makeText(getContext(), getString(message), Toast.LENGTH_LONG).show();
        }
    }

    private void showBookInfoOnUI() {
        showBookInfo();
        try {
            String bookTitle = BOOK_INFO.get(Constants.Book.TITLE);
            tvBookTitle.setText(bookTitle);

            String bookSubTitle = BOOK_INFO.get(Constants.Book.SUBTITLE);
            tvBookSubTitle.setText(bookSubTitle);

            Ion.with(getContext())
                    .load(BOOK_INFO.get(Constants.Book.IMG_URL))
                    .withBitmap()
                    .placeholder(R.drawable.book_default)
                    .error(R.drawable.book_default)
                    .intoImageView(ivBookCover);

            if (BOOK_INFO.containsKey(Constants.Book.AUTHORS)) {
                JSONArray authors = new JSONArray(BOOK_INFO.get(Constants.Book.AUTHORS));
                String s = "";
                for (int i = 0; i < authors.length(); i++) {
                    s += authors.getString(i);
                    if (i == authors.length() - 1 && i != 0) {
                        s += ",";
                    }
                }
                tvAuthors.setText(s);
            }

            if (BOOK_INFO.containsKey(Constants.Book.CATEGORIES)) {
                JSONArray categories = new JSONArray(BOOK_INFO.get(Constants.Book.CATEGORIES));
                String s = "";
                for (int i = 0; i < categories.length(); i++) {
                    s += categories.getString(i);
                    if (i == categories.length() - 1 && i != 0) {
                        s += ",";
                    }
                }
                tvCategories.setText(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            etEan.setText(scanResult.getContents());
        }
        // else continue with any other code you need in the method
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
                AlertDialog.Builder builderSave = new AlertDialog.Builder(getContext());
                builderSave.setTitle(R.string.dialog_title_save)
                        .setMessage(R.string.dialog_message_save)
                        .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Utility.saveBook(getContext(), BOOK_INFO);
                                Toast.makeText(getContext(),
                                        getString(R.string.book_saved, BOOK_INFO.get(Constants.Book.TITLE)),
                                        Toast.LENGTH_SHORT).show();
                                etEan.setText("");
                            }
                        })
                        .setNegativeButton(R.string.dialog_button_dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.clear_button:
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
