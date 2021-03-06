/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.activities.habits.list;


import android.content.*;

import org.isoron.androidbase.activities.*;
import org.isoron.uhabits.R;
import org.isoron.uhabits.activities.common.dialogs.*;
import org.isoron.uhabits.activities.habits.edit.*;
import org.isoron.uhabits.core.commands.*;
import org.isoron.uhabits.core.models.*;
import org.isoron.uhabits.core.preferences.*;
import org.isoron.uhabits.core.ui.*;
import org.isoron.uhabits.core.ui.callbacks.*;
import org.isoron.uhabits.intents.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.junit.*;

import java.util.*;

import static org.isoron.uhabits.activities.habits.list.ListHabitsScreen.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class ListHabitsScreenTest
{
    private BaseActivity activity;

    private ListHabitsRootView rootView;

    private ListHabitsScreen screen;

    private ListHabitsController controller;

    private Habit habit;

    private Intent intent;

    private ConfirmDeleteDialogFactory confirmDeleteDialogFactory;

    private IntentFactory intentFactory;

    private CommandRunner commandRunner;

    private ColorPickerDialogFactory colorPickerDialogFactory;

    private EditHabitDialogFactory dialogFactory;

    private ThemeSwitcher themeSwitcher;

    private Preferences prefs;

    private CommandParser commandParser;

    @Before
    public void setUp()
    {
        activity = mock(ListHabitsActivity.class);
        commandRunner = mock(CommandRunner.class);
        rootView = mock(ListHabitsRootView.class);
        intentFactory = mock(IntentFactory.class);
        themeSwitcher = mock(ThemeSwitcher.class);
        confirmDeleteDialogFactory = mock(ConfirmDeleteDialogFactory.class);
        colorPickerDialogFactory = mock(ColorPickerDialogFactory.class);
        dialogFactory = mock(EditHabitDialogFactory.class);
        prefs = mock(Preferences.class);
        commandParser = mock(CommandParser.class);

        screen = spy(new ListHabitsScreen(activity, commandRunner, rootView,
            intentFactory, themeSwitcher, confirmDeleteDialogFactory,
            colorPickerDialogFactory, dialogFactory, prefs));

        doNothing().when(screen).showMessage(anyInt());

        controller = mock(ListHabitsController.class);
        screen.setController(controller);

        habit = mock(Habit.class);
        intent = mock(Intent.class);
    }

//    @Test
//    public void testCreateHabitScreen()
//    {
//        CreateBooleanHabitDialog dialog = mock(CreateBooleanHabitDialog.class);
//        when(createHabitDialogFactory.create()).thenReturn(dialog);
//
//        screen.showCreateHabitScreen();
//
//        verify(activity).showDialog(eq(dialog), any());
//    }

    @Test
    public void testOnAttached()
    {
        screen.onAttached();
        verify(commandRunner).addListener(screen);
    }

    @Test
    public void testOnCommand()
    {
        Command c = mock(DeleteHabitsCommand.class);
        screen.onCommandExecuted(c, null);
        verify(screen).showMessage(R.string.toast_habit_deleted);
    }

    @Test
    public void testOnDetach()
    {
        screen.onDettached();
        verify(commandRunner).removeListener(screen);
    }

    @Test
    public void testOnResult_bugReport()
    {
        screen.onResult(REQUEST_SETTINGS, RESULT_BUG_REPORT, null);
        verify(controller).onSendBugReport();
    }

    @Test
    public void testOnResult_exportCSV()
    {
        screen.onResult(REQUEST_SETTINGS, RESULT_EXPORT_CSV, null);
        verify(controller).onExportCSV();
    }

    @Test
    public void testOnResult_exportDB()
    {
        screen.onResult(REQUEST_SETTINGS, RESULT_EXPORT_DB, null);
        verify(controller).onExportDB();
    }

    @Test
    public void testOnResult_importData()
    {
        screen.onResult(REQUEST_SETTINGS, RESULT_IMPORT_DATA, null);
        testShowImportScreen();
    }

    @Test
    public void testShowAboutScreen() throws Exception
    {
        when(intentFactory.startAboutActivity(activity)).thenReturn(intent);
        screen.showAboutScreen();
        verify(activity).startActivity(eq(intent));
    }

    @Test
    public void testShowColorPicker()
    {
        ColorPickerDialog picker = mock(ColorPickerDialog.class);
        when(colorPickerDialogFactory.create(999)).thenReturn(picker);
        OnColorPickedCallback callback = mock(OnColorPickedCallback.class);

        screen.showColorPicker(999, callback);

        verify(activity).showDialog(eq(picker), any());
        verify(picker).setListener(callback);
    }

    @Test
    public void testShowDeleteConfirmationScreen()
    {
        OnConfirmedCallback callback = mock(OnConfirmedCallback.class);
        ConfirmDeleteDialog dialog = mock(ConfirmDeleteDialog.class);
        when(confirmDeleteDialogFactory.create(callback)).thenReturn(dialog);

        screen.showDeleteConfirmationScreen(callback);

        verify(activity).showDialog(dialog);
    }

    @Test
    public void testShowEditHabitScreen()
    {
        EditHabitDialog dialog = mock(EditHabitDialog.class);
        when(dialogFactory.edit(habit)).thenReturn(dialog);
        screen.showEditHabitsScreen(Collections.singletonList(habit));
        verify(activity).showDialog(eq(dialog), any());
    }

    @Test
    public void testShowFAQScreen()
    {
        when(intentFactory.viewFAQ(activity)).thenReturn(intent);
        screen.showFAQScreen();
        verify(activity).startActivity(intent);
    }

    @Test
    public void testShowHabitScreen()
    {
        when(intentFactory.startShowHabitActivity(activity, habit)).thenReturn(
            intent);
        screen.showHabitScreen(habit);
        verify(activity).startActivity(intent);
    }

    @Test
    public void testShowImportScreen()
    {
        when(intentFactory.openDocument()).thenReturn(intent);
        screen.showImportScreen();
        verify(activity).startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }

    @Test
    public void testShowIntroScreen()
    {
        when(intentFactory.startIntroActivity(activity)).thenReturn(intent);
        screen.showIntroScreen();
        verify(activity).startActivity(intent);
    }

    @Test
    public void testShowSettingsScreen()
    {
        when(intentFactory.startSettingsActivity(activity)).thenReturn(intent);
        screen.showSettingsScreen();
        verify(activity).startActivityForResult(eq(intent), anyInt());
    }

    @Test
    public void testApplyTheme()
    {
        screen.applyTheme();
        verify(activity).restartWithFade(ListHabitsActivity.class);
    }
}