/*
 * Copyright 2016 Chris Renke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisrenke.giv.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.chrisrenke.giv.GravityImageView;
import java.util.ArrayList;
import java.util.List;

public class GivSample extends Activity {

  private final OnClickListener clickListener = new OnClickListener() {

    private final Integer[] DRAWABLE_IDS = {
        R.drawable.dinosaur_160, //
        R.drawable.dinosaur_2000, //
        R.drawable.dinosaur_120, //
        R.drawable.dinosaur_700
    };

    @SuppressWarnings("deprecation") //
    @Override public void onClick(View v) {
      int tagIndex = v.getTag() == null ? 3 : (int) v.getTag();
      int arrayIndex = tagIndex % DRAWABLE_IDS.length;
      v.setTag(arrayIndex + 1);

      GravityImageView gravityImageView = (GravityImageView) v;
      gravityImageView.setImageDrawable(v.getResources().getDrawable(DRAWABLE_IDS[(arrayIndex)]));
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.giv_sample_layout);
    List<GravityImageView> gravityImageViews = new ArrayList<>();
    findImageViews((ViewGroup) findViewById(android.R.id.content), gravityImageViews);
    for (GravityImageView gravityImageView : gravityImageViews) {
      gravityImageView.setOnClickListener(clickListener);
    }
  }

  /** Recurse through all the views and add all the {@link GravityImageView}s to the list. */
  private void findImageViews(@NonNull ViewGroup hostContainer,
      List<GravityImageView> gravityImageViews) {
    for (int i = 0; i < hostContainer.getChildCount(); i++) {
      View child = hostContainer.getChildAt(i);
      if (child instanceof ViewGroup) {
        findImageViews((ViewGroup) child, gravityImageViews);
      } else if (child instanceof GravityImageView) {
        gravityImageViews.add((GravityImageView) child);
      }
    }
  }
}
