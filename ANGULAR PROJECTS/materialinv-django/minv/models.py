from django.db import models

# Create your models here.
# id integer key string price double created_at


class Material(models.Model):
    def __str__(self):
        return self.key
    key = models.CharField(max_length=200)
    price = models.FloatField()
    created_at = models.DateTimeField("Created date")
    quantity = models.FloatField(default=0)

class UnitOfMeasurement(models.Model):
    def __str__(self):
        return self.measurement_unit

    measurement_unit = models.CharField(max_length=100)


class Entry(models.Model):
    def __str__(self):
        return self.ticket_id
    ticket_id = models.IntegerField("Delivery / Issue Ticket No.")
    material = models.ForeignKey(Material, on_delete=models.CASCADE)
    quantity = models.FloatField()
    unit_of_measurement = models.ForeignKey(UnitOfMeasurement, on_delete=models.CASCADE)
    created_at = models.DateTimeField("Created date", auto_now=True)
    class EntryType(models.TextChoices):
        Inward = 'INWARD'
        Issue = 'Issue'
    entry_type = models.CharField("Entry Type", max_length=10, choices=EntryType.choices, default=EntryType.Inward)
    # user_id = models.IntegerField()
    # user_id = models.ForeignKey(Users)
