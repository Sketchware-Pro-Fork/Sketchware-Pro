package mod.hilal.saif.asd;

import static pro.sketchware.utility.SketchwareUtil.getDip;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.besome.sketch.editor.LogicEditorActivity;
import pro.sketchware.R;

import a.a.a.Lx;
import a.a.a.Ss;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.databinding.DialogAsdBinding;
import mod.hey.studios.code.SrcCodeEditor;
import mod.hey.studios.util.Helper;

public class AsdDialog extends Dialog implements DialogInterface.OnDismissListener {

    private static SharedPreferences pref;
    private final Activity activity;
    private String content;
    
    private DialogAsdBinding binding;

    public AsdDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogAsdBinding.inflate(LayoutInflater.from(activity));
        setContentView(binding.getRoot());

        binding.editor.setTypefaceText(Typeface.MONOSPACE);
        binding.editor.setEditorLanguage(new JavaLanguage());
        binding.editor.setText(content);
        SrcCodeEditor.loadCESettings(activity, binding.editor, "dlg");
        pref = SrcCodeEditor.pref;

        Menu menu = binding.toolbar.getMenu();
        MenuItem itemWordwrap = menu.findItem(R.id.action_word_wrap);
        MenuItem itemAutocomplete = menu.findItem(R.id.action_autocomplete);
        MenuItem itemAutocompleteSymbolPair = menu.findItem(R.id.action_autocomplete_symbol_pair);

        itemWordwrap.setChecked(pref.getBoolean("dlg_ww", false));
        itemAutocomplete.setChecked(pref.getBoolean("dlg_ac", false));
        itemAutocompleteSymbolPair.setChecked(pref.getBoolean("dlg_acsp", true));

        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_undo) {
                binding.editor.undo();
            } else if (id == R.id.action_redo) {
                binding.editor.redo();
            } else if (id == R.id.action_pretty_print) {
                StringBuilder sb = new StringBuilder();
                String[] split = binding.editor.getText().toString().split("\n");
                for (String s : split) {
                    String trim = (s + "X").trim();
                    sb.append(trim.substring(0, trim.length() - 1));
                    sb.append("\n");
                }
                boolean failed = false;
                String code = sb.toString();
                try {
                    code = Lx.j(code, true);
                } catch (Exception e) {
                    failed = true;
                    SketchwareUtil.toastError("Your code contains incorrectly nested parentheses");
                }
                if (!failed) {
                    binding.editor.setText(code);
                }
            } else if (id == R.id.action_switch_language) {
                SketchwareUtil.toast("Currently not supported, sorry!");
            } else if (id == R.id.action_switch_theme) {
                SrcCodeEditor.showSwitchThemeDialog(activity, binding.editor, (dialog, which) -> {
                    SrcCodeEditor.selectTheme(binding.editor, which);
                    AsdDialog.pref.edit().putInt("dlg_theme", which).apply();
                    
                    dialog.dismiss();
                });
            } else if (id == R.id.action_word_wrap) {
                item.setChecked(!item.isChecked());
                binding.editor.setWordwrap(item.isChecked());
                pref.edit().putBoolean("dlg_ww", item.isChecked()).apply();
            } else if (id == R.id.action_autocomplete_symbol_pair) {
                item.setChecked(!item.isChecked());
                binding.editor.getProps().symbolPairAutoCompletion = item.isChecked();
                pref.edit().putBoolean("dlg_acsp", item.isChecked()).apply();
            } else if (id == R.id.action_autocomplete) {
                item.setChecked(!item.isChecked());
                binding.editor.getComponent(EditorAutoCompletion.class).setEnabled(item.isChecked());
                pref.edit().putBoolean("dlg_ac", item.isChecked()).apply();
            } else if (id == R.id.action_paste) {
                binding.editor.pasteText();
            }
            return true;
        });

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOnDismissListener(this);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        pref.edit().putInt("dlg_ts", (int) (binding.editor.getTextSizePx() / activity.getResources().getDisplayMetrics().scaledDensity)).apply();
    }
    
    public void saveListener(final LogicEditorActivity logicEditorActivity, boolean z, Ss ss, AsdDialog asdDialog) {
        binding.save.setOnClickListener(new AsdHandlerCodeEditor(logicEditorActivity, z, ss, asdDialog, binding.editor));
    }

    public void cancelListener(final AsdDialog asdDialog) {
        binding.cancel.setOnClickListener(Helper.getDialogDismissListener(asdDialog));
    }

    public void setContent(final String content) {
        this.content = content;
    }
}