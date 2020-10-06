package id.ac.untad.dicodingfundamental2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<User> listItems = new ArrayList<>();
    private RecyclerView rvUser;
    private ProgressBar progressBar;
    private ArrayList<User> list = new ArrayList<>();
    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        progressBar = findViewById(R.id.progressBar);
        rvUser = findViewById(R.id.rv_user);
        rvUser.setHasFixedSize(true);
        getListUser();
        showRecyclerList();
    }


    public void showRecyclerList() {
        adapter = new UserListAdapter(list);
        rvUser = findViewById(R.id.rv_user);
        rvUser.setHasFixedSize(true);
        rvUser.setLayoutManager(new LinearLayoutManager(this));
        rvUser.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvUser.setAdapter(adapter);
    }


    private void getListUser() {
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.github.com/users";
        client.addHeader("Authorization", "token 4132aeea442eeb21b45a8debf416f36ea81fb5f2");
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                String response = new String(responseBody);
                Log.d("JSON", response);
                try {
                    JSONArray dataArray = new JSONArray(response);
                    ArrayList<User> listUser = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        String username = dataObject.getString("login");
                        String avatar = dataObject.getString("avatar_url");
                        String organization = dataObject.getString("type");
                        User user = new User();
                        user.setLogin(username);
                        user.setAvatar_url(avatar);
                        user.setOrganizations_url(organization);
                        listUser.add(user);
                        adapter = new UserListAdapter(listUser);
                        rvUser.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e("JSON", "JSON ERROR EXCEPTION", e);
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                String errorMessage;
                switch (statusCode) {
                    case 401:
                        errorMessage = statusCode + " : 401  Bad Request";
                        break;

                    case 403:
                        errorMessage = statusCode + " 403 : Forbidden";
                        break;

                    case 404:
                        errorMessage = statusCode + " :404  Not Found";
                        break;

                    default:
                        errorMessage = statusCode + " : " + error.getMessage();
                        break;
                }
                error.printStackTrace();
            }
        });

    }


    public void getSearchDataApi(String username) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.github.com/search/users?q=" + username;
        client.addHeader("Authorization", "token 4132aeea442eeb21b45a8debf416f36ea81fb5f2");
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d("getSearchDataApi Class", result);
                listItems.clear();
                try {
                    JSONObject dataObject = new JSONObject(result);
                    JSONArray items = dataObject.getJSONArray("items");
                    ArrayList<User> listSearch = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        Log.d(TAG, "Sukses: " + items);
                        JSONObject item = items.getJSONObject(i);
                        String username = item.getString("login");
                        String avatar = item.getString("avatar_url");
                        String repository = item.getString("type");
                        User user = new User();
                        user.setLogin(username);
                        user.setAvatar_url(avatar);
                        user.setOrganizations_url(repository);
                        listSearch.add(user);
                        adapter = new UserListAdapter(listSearch);
                        rvUser.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Error Gan");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage;

                switch (statusCode) {
                    case 401:
                        errorMessage = statusCode + " : Bad Request";
                        break;

                    case 403:
                        errorMessage = statusCode + " : Forbidden";
                        break;

                    case 404:
                        errorMessage = statusCode + " : Not Found";
                        break;

                    default:
                        errorMessage = statusCode + " : " + error.getMessage();
                        break;
                }
                Log.d(TAG, errorMessage);
                error.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            SearchView searchView = (SearchView) (menu.findItem(R.id.search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.Cari));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    showRecyclerList();
                    getSearchDataApi(newText);
                    return false;
                }
            });
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.language:
                Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(mIntent);
                return true;
            default:
                return true;
        }
    }


}