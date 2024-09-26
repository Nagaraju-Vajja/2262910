# Generated by Django 4.2.9 on 2024-01-09 03:46

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('minv', '0008_entry_entry_type_delete_stock'),
    ]

    operations = [
        migrations.AlterField(
            model_name='entry',
            name='entry_type',
            field=models.CharField(choices=[('INWARD', 'Inward'), ('Issue', 'Issue')], default='INWARD', max_length=10, verbose_name='Entry Type'),
        ),
    ]
