from django.urls import path

from . import views

urlpatterns = [
    path("", views.index, name="index"),
    # ex: /polls/5/
    path("entries/", views.detail, name="entries"),
    path("entry/", views.entry_form, name="entry"),
    path("stock/", views.stock, name="stock"),
    path('material_list/', views.material_list, name='material_list'),
]
