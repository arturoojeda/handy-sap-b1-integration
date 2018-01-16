import com.handy.Sync
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class BootStrap {

    def init = { servletContext ->

        Date today = new Date()
        DateTimeZone guadalajaraTimeZone = org.joda.time.DateTimeZone.forID("America/Mexico_City")
        DateTime nowGuadalajara = new DateTime( guadalajaraTimeZone )

        def sync = Sync.get(1)

        if(sync){
            boolean changes

            if(!sync.salesOrderLastUpdated){
                sync.salesOrderLastUpdated = today
                changes = true
            }

            if(!sync.salesOrderLastUpdatedTime){
                sync.salesOrderLastUpdatedTime = nowGuadalajara.getMillis()
                changes = true
            }

            if(!sync.checkInvoiceLastUpdated){
                sync.checkInvoiceLastUpdated = today
                changes = true
            }

            if(!sync.aacuerdoOffset){
                sync.aacuerdoOffset = 0
                changes = true
            }
            if(!sync.alacuerdoOffset){
                sync.alacuerdoOffset = 0
                changes = true
            }

            if(changes)
                sync.save(failOnError: true);

        }else{
            sync = new Sync()
            sync.salesOrderLastUpdated = today
            sync.checkInvoiceLastUpdated = today
            sync.salesOrderLastUpdatedTime = nowGuadalajara.getMillis()
            sync.save(failOnError: true);
        }
    }

    def destroy = {
    }
}
