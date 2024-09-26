from django import forms
from crispy_forms.helper import FormHelper
from crispy_forms.layout import Layout, Submit, Column
from minv.models import Entry, Material


class EntryForm(forms.ModelForm):
    class Meta:
        model = Entry
        fields = ['entry_type', 'ticket_id', 'material', 'quantity', 'unit_of_measurement']

    def __init__(self, *args, **kwargs):
        super(EntryForm, self).__init__(*args, **kwargs)
        self.helper = FormHelper()
        self.helper.form_class = 'form-horizontal'
        self.helper.label_class = 'col-md-4'
        self.helper.field_class = 'col-md-4'
        self.helper.layout = Layout(
            'entry_type',
            'ticket_id',
            'material'
            'quantity',
            'unit_of_measurement',
            # Row(
            #     # Column('last_billed', css_class='form-group col-md-4 mb-0'),
            #     Column('ticket_id', css_class='form-group col-md-1 mb-4'),
            #     Column('created_at', css_class='form-group col-md-1 mb-0'),
            #     Column('quantity', css_class='form-group col-md-1 mb-0'),
            #     # Column('unit_of_measurement', css_class='form-group col-md-4 mb-0'),
            #     css_class='form-row'
            # ),
            Submit('submit', 'Save')
        )


class MaterialForm(forms.ModelForm):
    class Meta:
        model = Material
        fields = ['key', 'price', 'created_at', 'quantity']

    def __init__(self, *args, **kwargs):
        super(MaterialForm, self).__init__(*args, **kwargs)
        self.helper = FormHelper()
        self.helper.form_class = 'form-horizontal'
        self.helper.label_class = 'col-md-4'
        self.helper.field_class = 'col-md-4'
        self.helper.layout = Layout(
            'key',
            'price',
            'created_at',
            'quantity',
            # Row(
            #     # Column('last_billed', css_class='form-group col-md-4 mb-0'),
            #     Column('ticket_id', css_class='form-group col-md-1 mb-4'),
            #     Column('created_at', css_class='form-group col-md-1 mb-0'),
            #     Column('quantity', css_class='form-group col-md-1 mb-0'),
            #     # Column('unit_of_measurement', css_class='form-group col-md-4 mb-0'),
            #     css_class='form-row'
            # ),
            Submit('submit', 'Save')
        )
