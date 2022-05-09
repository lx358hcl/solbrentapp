package com.example.in2000_team32.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.in2000_team32.R

class ViewPagerAdapterHome(val context: Context) : PagerAdapter() {
    var layoutInflater: LayoutInflater? = null


    private val headArray = arrayOf(
        "Solbeskyttelse",
        "Vinterblekhet",
        "Smøretips",
        "Unngå solen",
        "Solskader varer",
        "UVA og UVB",
        "UVA-stråler",
        "UVB-stråler",
        "Sterkest stråling",
        "Refleksjon"
    )


    private val descriptionArray = arrayOf(
        "Mye sol og solarium kan være helsefarlig og gi deg hudkreft. For å redusere hudkreftrisikoen kan du beskytte deg med klær og solkrem og begrense tiden du er ute når sola skinner sterkest.",
        "Solbeskyttelse er ekstra viktig når du er vinterblek og hvis du har lys hud.",
        "Bruk helst minimum faktor 30. Vær raus med kremen, bruk en god håndfull til en hel kropp. Smør deg før du går ut, gjerne to ganger. Gjenta etter to timer, og hvis du har badet eller svettet bort kremen. Du bør ikke være lenge i sterk sol selv om du har smurt deg.",
        "Din beste solbeskyttelse er å være mindre i den sterke sola midt på dagen. Risiko for hudkreft øker spesielt når du blir solbrent, men også når du får mye sol uten å bli brent. Mye UV-stråler gir dessuten rynker og pigmentflekker.",
        "Huden din glemmer ikke solskader du har fått. Mye sol og solforbrenninger, særlig i barneårene, kan føre til hudkreft senere i livet. Sol og solarium er årsaken til rundt 90 prosent av hudkrefttilfellene i Norge.",
        "Vi utsettes for to typer ultrafiolette stråler, også kalt UV-stråler, fra sol og solarium: UVA-stråler og UVB-stråler. For mye av begge disse stråletypene kan gi hudkreft.",
        "UVA-stråler gir oss brunfarge, rynker og hudkreft. Det er ekstra mye UVA-stråler i solarier, så mye at de kan gjøre oss solbrente.",
        "UVB-stråler gjør oss solbrente, kan gi hudkreft, herder huden over tid og gir en viss UV-beskyttelse. Det gir også D-vitamin.",
        "Sola og uv-strålingen er sterkest om sommeren, midt på dagen, på høyfjellet og jo nærmere ekvator du er.",
        "Vann, sand og snø reflekterer og forsterker strålingen som treffer oss."


    )


    override fun getCount(): Int {
        return descriptionArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slider_home,container,false)
        val txt_head = view.findViewById<TextView>(R.id.textHeader)
        val txt_desc = view.findViewById<TextView>(R.id.textForklaring)


        txt_head.text = headArray[position]
        txt_desc.text = descriptionArray[position]



        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}