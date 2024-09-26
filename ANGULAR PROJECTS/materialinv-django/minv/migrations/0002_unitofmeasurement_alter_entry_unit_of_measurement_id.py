# Generated by Django 5.0.1 on 2024-01-05 07:21

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('minv', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='UnitOfMeasurement',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('measurement_unit', models.CharField(max_length=100)),
            ],
        ),
        migrations.AlterField(
            model_name='entry',
            name='unit_of_measurement_id',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='minv.unitofmeasurement'),
        ),
    ]
