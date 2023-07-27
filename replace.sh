#!/bin/bash

find . -name '*evolution_*' -type f -exec bash -c 'r=$1;shift;for p;do o=${p##*/};d=${p%/*};mv -- "$p" "$d/${o/evolution_/"$r"}";done' _ sigma_ {} \+;
