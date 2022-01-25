package com.neoguri.pensionlottery.data.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class JsoupRequest(val context: Context) {
    // https://stackoverflow.com/questions/47733379/java-io-ioexception-mark-has-been-invalidated-when-parsing-website-with-jsoup
    // GET: .execute().bufferUp().parse();
    // POST: .method(Connection.Method.POST).execute().bufferUp().parse();

    fun crawl(
        url: String,
        callback: (Element, Element, check: Boolean) -> Any?
    ) {
        // for ui change
        // callback differs by domain

        var wrappingGet: Pair<Element, Element>? = null

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    wrappingGet =
                        Jsoup.connect(url).execute().bufferUp().parse().run {
                            head() to body()
                        }

                }
            }
            try {
                val (head, body) = wrappingGet!!

                callback(head, body, true)

            } catch (err: Exception) {
                val html = "<head></head> <body> <span class=\"log\">오류</span> </body>"
                val doc: Document = Jsoup.parseBodyFragment(html)
                val head: Element = doc.head()
                val body: Element = doc.body()

                callback(head, body, false)

                //Toast.makeText(context, "네트워크 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
                //delay(2000L)
                //exitProcess(0)
            }
        }
    }
}