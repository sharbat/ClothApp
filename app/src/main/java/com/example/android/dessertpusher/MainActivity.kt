/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertpusher

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import com.example.android.dessertpusher.databinding.ActivityMainBinding
import timber.log.Timber

/** onSaveInstanceState Bundle Keys **/
const val KEY_REVENUE = "revenue_key"
const val KEY_CLOTH_SOLD = "cloth_sold_key"
const val KEY_TIMER_SECONDS = "timer_seconds_key"

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private var revenue = 0
    private var clothSold = 0
    private lateinit var clothTimer: ClothTimer

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Cloth(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all clothes, in order of when they start being produced
    private val allClothes = listOf(
        Cloth(R.drawable.flatshoes, 5, 0),
        Cloth(R.drawable.jumpsuit, 10, 5),
        Cloth(R.drawable.midiskirt, 15, 20),
        Cloth(R.drawable.pamelahat, 30, 50),
        Cloth(R.drawable.scarf, 50, 100),
        Cloth(R.drawable.skirt, 100, 200),
        Cloth(R.drawable.totebag, 500, 500),
        Cloth(R.drawable.tshirt, 1000, 1000),
        Cloth(R.drawable.short1, 2000, 2000),
        Cloth(R.drawable.overall, 3000, 4000)

    )
    private var currentCloth = allClothes[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate Called")

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.clothButton.setOnClickListener {
            onClothClicked()
        }
        binding.doubleButton.setOnClickListener {
            onButtonClicked()
        }

        // Setup dessertTimer, passing in the lifecycle
        clothTimer = ClothTimer(this.lifecycle)

        // If there is a savedInstanceState bundle, then you're "restarting" the activity
        // If there isn't a bundle, then it's a "fresh" start
        if (savedInstanceState != null) {
            // Get all the game state information from the bundle, set it
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            clothSold = savedInstanceState.getInt(KEY_CLOTH_SOLD, 0)
            clothTimer.secondsCount = savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
            showCurrentCloth()

        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = clothSold

        // Make sure the correct dessert is showing
        binding.clothButton.setImageResource(currentCloth.imageId)
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onClothClicked() {

        // Update the score
        revenue += currentCloth.price
        clothSold++

        binding.revenue = revenue
        binding.amountSold = clothSold

        // Show the next dessert
        showCurrentCloth()
    }
    private fun onButtonClicked() {

        // Update the score
        revenue *= 2
        clothSold*=2

        binding.revenue = revenue
        binding.amountSold = clothSold

        // Show the next dessert
        showCurrentCloth()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentCloth() {
        var newCloth = allClothes[0]
        for (cloth in allClothes) {
            if (clothSold >= cloth.startProductionAmount) {
                newCloth = cloth
            }
            // The list of Clothes is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newCloth != currentCloth) {
            currentCloth = newCloth
            binding.clothButton.setImageResource(newCloth.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_text, clothSold, revenue))
                .setType("text/plain")
                .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Called when the user navigates away from the app but might come back
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_CLOTH_SOLD, clothSold)
        outState.putInt(KEY_TIMER_SECONDS, clothTimer.secondsCount)
        Timber.i("onSaveInstanceState Called")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Timber.i("onRestoreInstanceState Called")
    }

    /** Lifecycle Methods **/
    override fun onStart() {
        super.onStart()
        Timber.i("onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Called")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart Called")
    }
}
