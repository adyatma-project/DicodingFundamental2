package id.ac.untad.dicodingfundamental2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class FollowersFragment extends Fragment {

    private RecyclerView rvUser;
    private ProgressBar progressBar;
    private ArrayList<User> list = new ArrayList<>();
    private UserListAdapter adapter;
    public static final String EXTRA_FOLLOWER = "extra_follower";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String login = getActivity().getIntent().getStringExtra(EXTRA_FOLLOWER);
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressBar);
        rvUser = view.findViewById(R.id.rv_list_followers);
        rvUser.setHasFixedSize(true);
        adapter = new UserListAdapter(list);
        rvUser.setHasFixedSize(true);
        rvUser.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvUser.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        rvUser.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.github.com/users/" + login + "/followers";
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

}

