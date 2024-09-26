from django.shortcuts import render, redirect
from django.http import HttpResponse
from django.template import loader

from minv.models import Material
from minv.forms import EntryForm, MaterialForm
from django.http import HttpResponseRedirect
from django.shortcuts import render

def index(request):
    return HttpResponse("Home path")


def detail(request):
    template = loader.get_template("minv/entryform.html")
    context = {
        "materials":  Material.objects.all().order_by("id")
    }

    return HttpResponse(template.render(context, request))


def stock(request):
    template = loader.get_template("minv/stocklist.html")
    context = {
        "materials":  Material.objects.filter(quantity__gt=0),
        "value": 0,
    }
    for mt in context["materials"]:
        mt.value = mt.price*mt.quantity

    return HttpResponse(template.render(context, request))


def entry_form(request):
    if request.method == 'POST':
        form = EntryForm(request.POST)
        if form.is_valid():
            entry_instance = form.save()

            # Update the Material model's quantity
            material = entry_instance.material
            if entry_instance.entry_type == "INWARD":
                material.quantity = material.quantity + entry_instance.quantity
            else:
                material.quantity = material.quantity - entry_instance.quantity
            material.save()
            return redirect('/minv/stock')  # Redirect to a view displaying the list of inventory items
    else:
        form = EntryForm()

    return render(request, 'minv/entryform.html', {'form': form})

def material_list(request):
    materials = Material.objects.all()
    form = MaterialForm()

    if request.method == 'POST':
        form = MaterialForm(request.POST)
        if form.is_valid():
            form.save()

    context = {
        'materials': materials,
        'form': form,
    }
    return render(request, 'minv/material_list.html', context)