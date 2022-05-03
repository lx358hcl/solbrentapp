package com.example.in2000_team32

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(val context: Context) : PagerAdapter() {
    var layoutInflater: LayoutInflater? = null

    val imgArray = arrayOf(
        R.drawable.applogo,
        R.drawable.mappin,
        R.drawable.hudfargebilde
    )

    val headArray = arrayOf(
        "Silent?",
        "Posisjon",
        "Hudfarge"
    )

    val descriptionArray = arrayOf(
        "Slipp å bli solbrent med solbrent?-appen. Få oversikt over UV-styrke, historikk, D-vitamin opptak og påminnelser om når du bør smøre deg basert på din hudfarge!",
        "For å hente UV-data og været der du er trenger vi posisjonen din. Vennligst velg nøyaktig presisjon når du får valget. Da vil alt fungere som det skal :)",
        "Velg hudfargen din! Dette er nødvendig for å regne ut hvor lenge det er til du blir solbrent og hvor mye D-vitamin du tar opp i timen. (Merk at tjukke klær vil redusere D-vitamin opptaket)"
    )

    val buttonText = arrayOf(
        "Neste side",
        "Neste side",
        "Åpne app"
    )

    override fun getCount(): Int {
        return headArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slider,container,false)
        val img = view.findViewById<ImageView>(R.id.image)
        val txt_head = view.findViewById<TextView>(R.id.textHeader)
        val txt_desc = view.findViewById<TextView>(R.id.textForklaring)
        val button = view.findViewById<Button>(R.id.onboardButton)

        img.setImageResource(imgArray[position])
        txt_head.text = headArray[position]
        txt_desc.text = descriptionArray[position]
        button.text = buttonText[position]


        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}