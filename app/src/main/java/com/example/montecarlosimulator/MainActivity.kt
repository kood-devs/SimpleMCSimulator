package com.example.montecarlosimulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.sqrt
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Parameters
        var typeVal: String
        var strikeVal: Double
        var spotVal: Double
        var expiryVal: Double
        var volVal: Double
        var rVal: Double
        var numPathsVal: Long

        // TODO: allow user manual input
        CalculationBtn.setOnClickListener {
            // Set user inputs
            typeVal = TypeTxt.text.toString()
            strikeVal = StrikeTxt.text.toString().toDouble()
            spotVal = SpotTxt.text.toString().toDouble()
            expiryVal = ExpiryTxt.text.toString().toDouble()
            volVal = VolTxt.text.toString().toDouble()
            rVal = InterestTxt.text.toString().toDouble()
            numPathsVal = NumPathsTxt.text.toString().toLong()

            // Main simulation
            val result = calculateOptionPrice(
                typeVal,
                strikeVal,
                spotVal,
                expiryVal,
                volVal,
                rVal,
                numPathsVal
            )
            ResultTxt.text = result.toString()
        }
    }
}

fun calculateOptionPrice(
    type: String,
    strike: Double,
    spot: Double,
    expiry: Double,
    vol: Double,
    r: Double,
    numberOfPaths: Long
): Double {
    // プレーンバニラオプションの価格計算をモンテカルロ法により計算する関数

    val rand = java.util.Random()  // prepare rand generator

    val variance = vol * vol * expiry
    val rootVariance = sqrt(variance)
    val itoCorrection = -0.5 * variance
    val movedSpot = spot * exp(r * expiry + itoCorrection)

    var thisSpot: Double
    var thisPayoff: Double
    var thisGaussian: Double
    var runningSum = 0.0

    for (i in 0..numberOfPaths) {
        thisGaussian = rand.nextGaussian()
        thisSpot = movedSpot * exp(rootVariance * thisGaussian)

        // call or put
        thisPayoff = if (type == "Call" || type == "call") {
            thisSpot - strike
        } else if (type == "Put" || type == "put") {
            strike - thisSpot
        } else {
            0.0  // return 0.0 if invalid payoff type
        }

        thisPayoff = max(thisPayoff, 0.0)
        runningSum += thisPayoff
    }

    var mean = runningSum / numberOfPaths
    mean *= exp(-r * expiry)
    return mean
}