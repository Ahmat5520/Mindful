package com.example.mindfulscreen

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var currentLang = "ug"

    private val translations = mapOf(
        "ug" to mapOf(
            "title" to "رەقەملىك ئەقىل - ئېكران ئانالىزى",
            "perm_btn" to "ئىشلىتىش ھوقۇقىنى ئېچىش",
            "analyze_btn" to "بۈگۈنكىنى تەھلىل قىلىش",
            "meaningful" to "ئەھمىيەتلىك ئىشلار",
            "wasted" to "ئىسراپ بولغان ۋاقىت",
            "hours" to "سائەت",
            "advice_title" to "AI ئەسكەرتىشى ۋە سەلبىي تەسىرى:",
            "advice_text" to "بۈگۈن كۆپ قىسمى قىسقا سىن ۋە كۆڭۈل ئېچىشقا كەتتى. بۇ زېھىننى پارچىلاپ، بىلىش ئېنېرگىيەڭىزنى خورىتىدۇ. ئەتە قىسقا سىننى 45 مىنۇتتىن ئاشۇرماي، روھىڭىزنى ئاسراڭ!"
        ),
        "en" to mapOf(
            "title" to "MindfulScreen - Screen Analysis",
            "perm_btn" to "Grant Usage Access",
            "analyze_btn" to "Analyze Today",
            "meaningful" to "Productive Time",
            "wasted" to "Wasted Time",
            "hours" to "hrs",
            "advice_title" to "AI Insight & Negative Impact:",
            "advice_text" to "Excessive passive screen time fractured your focus today. Keep short videos under 45 minutes tomorrow to reclaim mental energy."
        ),
        "zh" to mapOf(
            "title" to "智能屏幕时间分析",
            "perm_btn" to "授予使用记录权限",
            "analyze_btn" to "分析今日数据",
            "meaningful" to "有意义时间",
            "wasted" to "碎片浪费时间",
            "hours" to "小时",
            "advice_title" to "AI 深度提醒与负面影响:",
            "advice_text" to "今日被动浏览过多，会削弱深度专注力并影响精力。建议明日将短视频时间控制在45分钟以内。"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sysLang = java.util.Locale.getDefault().language
        currentLang = when (sysLang) {
            "zh" -> "zh"
            "en" -> "en"
            else -> "ug"
        }

        renderUI()
    }

    private fun renderUI() {
        val t = translations[currentLang] ?: translations["ug"]!!
        val isRtl = currentLang == "ug"

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F172A"))
            setPadding(40, 60, 40, 40)
            layoutDirection = if (isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
        }

        val langSpinner = Spinner(this).apply {
            val languages = arrayOf("ئۇيغۇرچە", "English", "中文")
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, languages)
            setSelection(if (currentLang == "ug") 0 else if (currentLang == "en") 1 else 2)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                    val selected = when (pos) {
                        0 -> "ug"
                        1 -> "en"
                        else -> "zh"
                    }
                    if (selected != currentLang) {
                        currentLang = selected
                        renderUI()
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
        rootLayout.addView(langSpinner)

        val titleView = TextView(this).apply {
            text = t["title"]
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 40, 0, 40)
        }
        rootLayout.addView(titleView)

        val permBtn = Button(this).apply {
            text = t["perm_btn"]
            setBackgroundColor(Color.parseColor("#334155"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
        rootLayout.addView(permBtn)

        val statsCard = CardView(this).apply {
            setCardBackgroundColor(Color.parseColor("#1E293B"))
            radius = 24f
            setContentPadding(40, 40, 40, 40)
            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 40, 0, 40) }
            layoutParams = cardParams
        }

        val cardContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val statsText = TextView(this).apply {
            text = "📊 65% ${t["wasted"]}  |  35% ${t["meaningful"]}"
            textSize = 18f
            setTextColor(Color.parseColor("#38BDF8"))
            setTypeface(null, Typeface.BOLD)
        }
        cardContent.addView(statsText)

        val detailText = TextView(this).apply {
            text = "4.2 ${t["hours"]} / 2.3 ${t["hours"]}"
            textSize = 14f
            setTextColor(Color.parseColor("#94A3B8"))
            setPadding(0, 10, 0, 30)
        }
        cardContent.addView(detailText)

        val adviceTitle = TextView(this).apply {
            text = t["advice_title"]
            textSize = 16f
            setTextColor(Color.parseColor("#F43F5E"))
            setTypeface(null, Typeface.BOLD)
        }
        cardContent.addView(adviceTitle)

        val adviceBody = TextView(this).apply {
            text = t["advice_text"]
            textSize = 14f
            setTextColor(Color.parseColor("#E2E8F0"))
            setPadding(0, 10, 0, 0)
            setLineSpacing(1.2f, 1.2f)
        }
        cardContent.addView(adviceBody)

        statsCard.addView(cardContent)
        rootLayout.addView(statsCard)

        val analyzeBtn = Button(this).apply {
            text = t["analyze_btn"]
            setBackgroundColor(Color.parseColor("#0284C7"))
            setTextColor(Color.WHITE)
            textSize = 16f
            setOnClickListener {
                calculateUsage(t)
            }
        }
        rootLayout.addView(analyzeBtn)

        setContentView(rootLayout)
    }

    private fun calculateUsage(t: Map<String, String>) {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            Toast.makeText(this, t["perm_btn"], Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            return
        }

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        val startTime = cal.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        var totalTimeMs = 0L
        for (stat in stats) {
            totalTimeMs += stat.totalTimeInForeground
        }

        val totalHours = totalTimeMs / (1000 * 60 * 60f)
        Toast.makeText(this, "بۈگۈنكى جەمئىي ئېكران ۋاقتى: ${String.format("%.1f", totalHours)} سائەت", Toast.LENGTH_LONG).show()
    }
}
