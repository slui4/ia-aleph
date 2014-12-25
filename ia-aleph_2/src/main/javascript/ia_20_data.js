/*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

var data = ia.data;

(function () {

    var factionsByCode = {};
    $.each(data.factions, function (i, faction) {
        factionsByCode[faction.code] = faction;
    });

    data.findFactionByCode = function (code) {
        return factionsByCode[code];
    };

    var troopersByCode = {};
    $.each(data.troopers, function (i, trooper) {
        troopersByCode[trooper.code] = trooper = $.extend({
            bsw: [],
            ccw: [],
            skills: [],
            equipments: [],
            isRegular: !trooper.irregular,
            isIrregular: trooper.irregular,
            isImpetuous: trooper.impetuosity === 'I',
            isFrenzy: trooper.impetuosity === 'F',
            isExtremelyImpetuous: trooper.impetuosity === 'E',
            hasCube: trooper.cube === 'C' || trooper.cube === '2',
            hasCube2: trooper.cube === '2'
        }, trooper);
        trooper.isHackable = trooper.type === 'REM' || trooper.type === 'TAG' || trooper.type === 'HI';// TODO
        trooper.options = $.map(trooper.options, function (option) {
            option = $.extend({
                bsw: [],
                ccw: [],
                swc: '',
                cost: '',
                skills: [],
                equipments: []
            }, option);
            option.allBsw = [].concat(trooper.bsw).concat(option.bsw);
            option.allCcw = [].concat(trooper.ccw).concat(option.ccw);
            option.allSkills = [].concat(trooper.skills).concat(option.skills);
            option.allEquipments = [].concat(trooper.equipments).concat(option.equipments);
            //TODO sort by range
            return option;
//            option.skillsAndEquipments = [].concat(option.skills || []).concat(option.equipments || []);
        });
    });

    var troopersByFaction = {};


    data.findTrooperByCode = function (code) {
        return troopersByCode[code];
    };

    data.findTroopersByFaction = function (factionCode) {
        if (!troopersByFaction[factionCode]) {
            troopersByFaction[factionCode] = [];
            $.each(data.troopers, function (i, trooper) {
                if (trooper.faction === factionCode) {
                    troopersByFaction[factionCode].push(trooper);
                }
            });
        }
        return troopersByFaction[factionCode];
    };


})();