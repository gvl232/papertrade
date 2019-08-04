package com.example.test

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.doAsync
import java.net.URL
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    val t1 = timer(name = "quoteUpdate", initialDelay = 0, period = 500) { getQuote() }

    var btcusdtlast = 0.00
    var money: Double = 50000.00
    var crypto: Double = 0.00


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.title = "Paper Trader"
        actionBar.subtitle = "A Binance-API paper trader prototype"

        val moneyView: TextView = findViewById(R.id.moneyView) as TextView
        moneyView.text = "$$money USDT            $crypto BTC"
        moneyView.setTextColor(Color.parseColor("#808000"))

        buyorder.isInvisible = true
        sellorder.isInvisible = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updateWallet(amountusdt: Double, amountbtc: Double) {
        val df = DecimalFormat("####.##")
        money += df.format(amountusdt).toDouble()
        crypto += df.format(amountbtc).toDouble()
        val moneyView: TextView = findViewById(R.id.moneyView) as TextView
        moneyView.text = df.format(money).toString() + "USDT" + "            "+ df.format(crypto) + "BTC"
    }

    fun sellOnClick(buttonView: View) {
        var price = editText3.text.toString().toDouble()
        var amount = editText4.text.toString().toDouble()

        if ((crypto - amount) >= 0) {
            if (price - btcusdtlast <= 0) {
                Toast.makeText(this, "Sold $amount BTC", Toast.LENGTH_SHORT).show()
                updateWallet(price * amount, -1.0*amount)
            } else {
                sellorder.text = "$amount BTC @ $price USDT"
                sellorder.isInvisible = false

            }
        } else{
            Toast.makeText(this, "Insufficient Funds", Toast.LENGTH_SHORT).show()
        }
    }

    fun buyOnClick(buttonView: View) {
        var price = editText2.text.toString().toDouble()
        var amount = editText5.text.toString().toDouble()

        if ((money - price * amount) >= 0) {
            if (price - btcusdtlast >= 0) {
                Toast.makeText(this, "Bought $amount BTC", Toast.LENGTH_SHORT).show()
                updateWallet(-1.0 * price * amount, amount)
            } else {
                buyorder.text = "$amount BTC @ $price USDT"
                buyorder.isInvisible = false

            }
        } else{
            Toast.makeText(this, "Insufficient Funds", Toast.LENGTH_SHORT).show()
        }
    }

    fun getQuote() {
        doAsync {
            val tickerString = URL("https://api.binance.com/api/v1/ticker/price?symbol=BTCUSDT").readText()
            val btcusdt = tickerString.substring(29, 36)
            btcusdtlast = tickerString.substring(29, 36).toDouble()
            val lastPriceView: TextView = findViewById(R.id.lastPriceView) as TextView
            lastPriceView.text = "$$btcusdt USDT"
        }
    }

    fun buy25(buttonView: View){
        editText5.setText((money*0.25/btcusdtlast).toString())
    }

    fun buy50(buttonView: View){
        editText5.setText((money*0.5/btcusdtlast).toString())
    }

    fun buy75(buttonView: View){
        editText5.setText((money*0.75/btcusdtlast).toString())
    }

    fun buy100(buttonView: View){
        editText5.setText((money*1/btcusdtlast).toString())
    }

    fun sell25(buttonView: View){
        editText4.setText((crypto*0.25).toString())
    }

    fun sell50(buttonView: View){
        editText4.setText((crypto*0.5).toString())
    }

    fun sell75(buttonView: View){
        editText4.setText((crypto*0.75).toString())
    }

    fun sell100(buttonView: View){
        editText4.setText((crypto*1).toString())
    }
}




