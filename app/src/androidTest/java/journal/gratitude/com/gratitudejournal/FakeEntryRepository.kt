package journal.gratitude.com.gratitudejournal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import journal.gratitude.com.gratitudejournal.model.Entry
import journal.gratitude.com.gratitudejournal.repository.EntryRepository
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.threeten.bp.LocalDate
import java.util.LinkedHashMap
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class FakeEntryRepository : EntryRepository {

    //mock of database
    var entriesDatabase: LinkedHashMap<LocalDate, Entry> = LinkedHashMap()

    override fun getEntry(date: LocalDate): LiveData<Entry> {
        val liveData = MutableLiveData<Entry>()

        val entry = entriesDatabase[date] ?: Entry(date, "")

        liveData.value = entry

        return liveData
    }

    override fun getAllEntries(): LiveData<List<Entry>> {
        val liveData = MutableLiveData<List<Entry>>()
        val list = mutableListOf<Entry>()
        entriesDatabase.forEach { (date, entry) -> list.add(entry) }

        liveData.value = list

        return liveData
    }

    override fun getWrittenDates(): LiveData<List<LocalDate>> {
        val liveData = MutableLiveData<List<LocalDate>>()
        liveData.value = entriesDatabase.keys.toList()

        return liveData
    }

    override suspend fun addEntry(entry: Entry) {
        entriesDatabase[entry.entryDate] = entry
    }

    override suspend fun addEntries(entries: List<Entry>) {
        for (entry in entries) {
            entriesDatabase[entry.entryDate] = entry
        }
    }

    override fun searchEntries(query: String): LiveData<PagedList<Entry>> {
        val liveData = MutableLiveData<PagedList<Entry>>()
        return if (query == "query with no result") {
            liveData.value = mockPagedList(emptyList())
            liveData
        } else {
            val results = listOf(Entry(LocalDate.now(), "Today's content"), Entry(LocalDate.of(2019, 11, 29), "Happy birthday, Alison!"))
            liveData.value = mockPagedList(results)
            liveData
        }
    }

    private fun <T> mockPagedList(list: List<T>): PagedList<T> {
        val pagedList = Mockito.mock(PagedList::class.java) as PagedList<T>
        Mockito.`when`(pagedList[ArgumentMatchers.anyInt()]).then { invocation ->
            val index = invocation.arguments.first() as Int
            list[index]
        }
        Mockito.`when`(pagedList.size).thenReturn(list.size)
        return pagedList
    }

}