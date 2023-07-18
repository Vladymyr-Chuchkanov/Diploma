Бібліотека для інтеграції інтерфейсу та функціоналу з відкриття та перегляду файлів електронних книг у додаток Android.  
На даний момент підтримаються файли форматів FB2, EPUB.  
Бібліотека надає функціонал навігації по сторінках та змісту, пошуку у тексті, зміни розміру та типу шрифтів. Також є можливість змінювати мову інтерфейсу.  

Для підключення бібліотеки завантажте папку та додайте її у кореневу папку вашого проекту.  
import com.chuchkanov.filereader.FileViewer;  
FileViewer fv = new FileViewer(findViewById(R.id.reader)); //має бути передано айді LinearLayout  
fv.setBook("Шлях до книги", this.getFilesDir());  

Для більш детального опису: https://docs.google.com/document/d/1feS7mvAkWA5PB-6508UNxOAMl8lvOCYJWDRATydn6Zk/edit?usp=sharing  
