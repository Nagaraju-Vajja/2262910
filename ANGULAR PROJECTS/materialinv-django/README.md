**Source code for management of material inventory**

After checking out the code, do the following:

1. Install Python `sudo apt install python`
2. Install pip `sudo apt install pip`
3. `pip install -r requirements.txt`
4. Run local migrations using `python manage.py makemigrations`
5. Sync local DB using `python manage.py migrate --run-syncdb`
6. Run the server using `python manage.py runserver`