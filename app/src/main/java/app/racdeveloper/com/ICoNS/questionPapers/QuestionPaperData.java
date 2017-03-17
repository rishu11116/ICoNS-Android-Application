package app.racdeveloper.com.ICoNS.questionPapers;

/**
 * Created by Rachit on 10/19/2016.
 */
public class QuestionPaperData {

        private int id;
        private String papername,papercontributor,paperurl;

        public QuestionPaperData(int id, String papername, String paperurl, String papercontributor) {
            this.id = id;
            this.papername = papername;
            this.papercontributor = papercontributor;
            this.paperurl = paperurl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPapername() {
            return papername;
        }

        public void setPapername(String papername) {
            this.papername = papername;
        }

        public String getPapercontributor() {
            return papercontributor;
        }

        public void setPapercontributor(String papercontributor) {
            this.papercontributor = papercontributor;
        }

        public String getPaperurl() {
            return paperurl;
        }

        public void setPaperurl(String paperurl) {
            this.paperurl = paperurl;
        }

}
