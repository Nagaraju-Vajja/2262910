# Generated by Django 5.0.1 on 2024-01-05 11:01

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('minv', '0003_rename_materials_material_stock'),
    ]

    operations = [
        migrations.RenameField(
            model_name='stock',
            old_name='material_id',
            new_name='material',
        ),
    ]
