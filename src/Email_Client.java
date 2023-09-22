import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email_Client {

    static Date today = new Date();
    public static void main(String[] args) throws ParseException, IOException {
//        LocalDate dat = LocalDate.now();
//        System.out.println(dat);
//        System.out.println(dat.getDayOfYear());

        Recipient_Arr recipient_arr = new Recipient_Arr();
        FileReader fileReader = new FileReader("clientList.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        for (String detail = bufferedReader.readLine(); detail != null; detail = bufferedReader.readLine()) {
            recipient_arr.add_details(detail);
        }
        for (Wishable w : recipient_arr.BirthDayRecords) { //Make a condition to stop the birthday wish to wished pwersons
            Birthday.SendBirthday_Mesg(w,today);
        }
        System.out.println("Enter option type: \n"
                + "1 - Adding a new recipient\n"
                + "2 - Sending an email\n"
                + "3 - Printing out all the recipients who have birthdays\n"
                + "4 - Printing out details of all the emails sent\n"
                + "5 - Printing out the number of recipient objects in the application");
        //        while (true) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:

                    System.out.println("Official: nimal,nimal@gmail.com,ceo\n"
                            + "Official_Friend: nimal,nimal@gmail.com,ceo,2000/10/11\n"
                            + "Personal: nimal,nim,nimal@gmail.com,2000/10/11");
                    System.out.println("Enter Your Details please: ");
                    String details = scanner.nextLine();
                    DataWriter dataWriter = new DataWriter();
                    dataWriter.Load_detail(details);

                    Recipient_Arr recipient_arr1 = new Recipient_Arr();
                    recipient_arr1.add_details(details);

                    break;
                case 2:
                    System.out.println("Input your email details");
                    System.out.println("input format - email,subject,content");
                    String email_details = scanner.nextLine();
                    String[] email_arr = email_details.split(",");

                    try {
                        String email = email_arr[0];
                        String subject = email_arr[1];
                        String content = email_arr[2];
                        SendMail.sendMail(subject, content, email);

                        Mails n_mails=new Mails(today,subject,content,email);
                        Mails.addMails(n_mails);
                        Serialization.Serialize(Mails.mailsArrayList);

                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case 3:

                    System.out.println("Input the date yyyy/MM/dd(ex: 2018/09/17) : ");
                    String date = scanner.nextLine();
                    Date givendate = new SimpleDateFormat("yyyy/MM/dd").parse(date);
//                        Recipient_Arr recipient_arr2 = new Recipient_Arr();
                    Birthday.Print_names(givendate, recipient_arr.BirthDayRecords);
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // code to print recipients who have birthdays on the given date
                    break;
                case 4:
                    System.out.println("Input Your date: ");
                    String date1 = scanner.nextLine();
                    Date date_1 = new SimpleDateFormat("yyyy/MM/dd").parse(date1);
                    ArrayList<Mails> mailsArrayList=Serialization.Deserialize();
                    for(int i=0;i<mailsArrayList.size();i++){
                        Mails mails=mailsArrayList.get(i);
                        if(mails.getDate().getDay()==today.getDay()&&mails.getDate().getMonth()==today.getMonth()){
                            System.out.println("Subject: "+mails.getSubject());
                            System.out.println("Content: "+mails.getContent());
                            System.out.println("Receiver Mail address: "+mails.getReceiver_mail_address());
                        }
                    }
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // code to print the details of all the emails sent on the input date
                    break;
                case 5:
//                    System.out.println("Count:" + recipient_arr.Records.size());
                    // code to print the number of recipient objects in the applic
                    // ation
                    System.out.println(Recipient.count);
                    break;
            }

        }
    }
}



// create more classes needed for the implementation (remove the  public access modifier from classes when you submit your code)

//---------------------Recipient-----------------------------//

abstract class Recipient{
    private String name;
    private String email;

    static int count=0; //Create static variable for count the total recipients

    public Recipient(String name, String email) {
        this.name = name;
        this.email = email;
        Recipient.count++; //increasing count when the recipient objects create

    }

//    public Recipient() {
//
//    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


}

//---------------------Wishable-----------------//
//Create a interface for  wishable recipients
interface Wishable {
    public String getBdayMsg();// method for get birthday mesg
    public String getName();//method for get recipient name
    public String getBirthday();//method for get birthday date
    public String getEmail();//method for get email address
}


class Official extends Recipient {
    private String designation;
    public Official(String name, String email, String designation) {
        super(name, email);
        this.designation = designation;
    }

    public String getDesignation() {//for get Designation of that official recipient
        return designation;
    }
}


//--------------------------Official_Friends---------------------------//

class Official_Friends extends Official implements Wishable{
    private String DOB;

    public Official_Friends(String name, String email, String designation, String DOB) {
        super(name, email, designation);
        this.DOB = DOB;
    }
    @Override
    public String getBdayMsg() {
        return "Wish you a Happy Birthday.";
    }//for get official_friends' birthday message
    @Override
    public String getBirthday() {
        return DOB;
    }

}


//----------------------Personal-------------------------------//
class Personal extends Recipient implements Wishable{
    private String nickname;
    private String DOB;

    public Personal(String name,  String nickname, String email,String DOB) {
        super(name, email);
        this.nickname = nickname;
        this.DOB = DOB;
    }
    @Override
    public String getBdayMsg() {
        return "Hugs and love on your birthday."; //get the personal recipient's birthday message
    }
    @Override
    public String getBirthday() {
        return DOB;
    }

}


//---------------------Birthday--------------------------//

//This birthday class for send birthday wishes and print Recipient names who has birthday on that given date
class Birthday {
    static Date date = new Date();//
    static ArrayList<String> wishes_sent_mails = new ArrayList<>();

    public static void SendBirthday_Mesg(Wishable w, Date today) throws ParseException {//Method for send birthday messages
        Date DOB = new SimpleDateFormat("yyyy/MM/dd").parse(w.getBirthday());//Convert the date of birth from string type to date type
        if (DOB.getDate() == today.getDate() && DOB.getMonth() == today.getMonth()) {//compare the Birthday date with todyadate
            SendMail.sendMail("Birth Day Wishes" + w.getName(), w.getBdayMsg(), w.getEmail());
            wishes_sent_mails.add(w.getEmail());
            Mails bd_mails = new Mails(date, "Birth Day Wishes", w.getBdayMsg(), w.getEmail());//create mail objects
            Mails.addMails(bd_mails);
            Serialization.Serialize(Mails.mailsArrayList);
        }
    }

    public static void Print_names(Date given_date, ArrayList<Wishable> wishables) throws ParseException {

        for (int i = 0; i < wishables.size(); i++) {
            Wishable w = wishables.get(i);
            String DOB_str = w.getBirthday();
            String Name = w.getName();
            Date DOB = new SimpleDateFormat("yyyy/MM/dd").parse(DOB_str);
            if (DOB.getDate() == given_date.getDate() && DOB.getMonth() == given_date.getMonth()) {
                System.out.println(Name);
            }
        }
    }
}



//--------------------------SendMail---------------------------//

//class for send mail to recipient
class SendMail {
    // Mail sending method
    public static void sendMail(String subject, String text, String recipient){

        final String username = "thevarasasangaran@gmail.com";
        final String password = "aeismrjnnzfveres";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(text+"\n"+"\nT.Sangaran");
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}



//------------------------Serialization-----------------------//

//class for serialize and deserialize the mail objects
class Serialization {
    static String filename = "Mail.ser";//ser file for write objects
    public static void Serialize(ArrayList<Mails> mailsArrayList) {//Method for serialize the objects
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            //Serializing from the array list to the ser file
            for(int i=0;i<mailsArrayList.size();i++){
                out.writeObject(mailsArrayList.get(i));
            }

            out.close();
            fileOut.close();
        } catch (IOException ex) {
        }
    }

    public static ArrayList<Mails> Deserialize() throws IOException {//Method for deserialize objects
        File file = new File(filename);
        if (file.length() != 0) {
            ArrayList<Mails> mailsArrayList = new ArrayList<>();//Create new array list for store deserializing objects
            Mails mails = null;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            //Read objects one by one
            while (true) {
                try {
                    mails = (Mails) in.readObject();
                    mailsArrayList.add(mails);
                } catch (EOFException | ClassNotFoundException e) {
                    break;
                }
            }

        }return Mails.mailsArrayList;//return the mailArraysList for other uses


    }

}


//-----------------DataWriter--------------------------//

//for update recipient data in a text file
class DataWriter {
    public void Load_detail(String detail){
        try{
            BufferedWriter myWriter = new BufferedWriter(new FileWriter("clientList.txt",true));
            myWriter.write(detail);
            myWriter.newLine();
            myWriter.flush();
            myWriter.close();
            System.out.println("Successfully Updated");

        }catch (IOException e){
            System.out.println(" An error occurred.");
            e.printStackTrace();
        }
    }
}



class Mails implements Serializable {
    static ArrayList<Mails> mailsArrayList=new ArrayList<>();
    private Date date;
    private String subject;
    private String content;
    private String Receiver_mail_address;

    public Mails(Date date, String subject, String content, String receiver_mail_address) {
        this.date = date;
        this.subject = subject;
        this.content = content;
        Receiver_mail_address = receiver_mail_address;
    }
    public static ArrayList<Mails> addMails(Mails mails){
        mailsArrayList.add(mails);
        return mailsArrayList;
    }
    public Date getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getReceiver_mail_address() {
        return Receiver_mail_address;
    }


}




class Recipient_Arr {
    ArrayList<Recipient> Records = new ArrayList<>();
    ArrayList<Wishable> BirthDayRecords = new ArrayList<>();

    public void add_details(String details) {
        String[] type_and_details = details.split(": ");
        String type = type_and_details[0];
        String[] data_arr=type_and_details[1].split(",");

        if(type.equals("Official")){
            Official official = new Official(data_arr[0],data_arr[1],data_arr[2]);
            Records.add(official);
        }

        else if(type.equals("Official_Friend")){
            Recipient official_Friends = new Official_Friends(data_arr[0],data_arr[1],data_arr[2],data_arr[3]);
            Records.add(official_Friends);
            BirthDayRecords.add((Wishable) official_Friends);

        }else if(type.equals("Personal")){
            Recipient personal = new Personal(data_arr[0],data_arr[1],data_arr[2],data_arr[3]);
            Records.add(personal);
            BirthDayRecords.add((Wishable) personal);

        }
    }

}

