    package com.example.agricom_it.ui;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ImageView;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;

    import com.example.agricom_it.R;

    public class AboutFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_about, container, false);

            ImageView myImage = view.findViewById(R.id.imgLogo);
            myImage.setOnClickListener(v -> {
                String url = "https://afrimart.virtuocloud.co.za/woofels.html";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });

            Button evoButton = view.findViewById(R.id.EvolutionAnywhere_button);

            evoButton.setOnClickListener(v -> {
                String url = "https://evolutionanywhere.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });

            Button virtButton = view.findViewById(R.id.VirtuoCloud_button);

            virtButton.setOnClickListener(v -> {
                String url = "https://virtuocloud.co.za";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });
            return view;
        }
    }